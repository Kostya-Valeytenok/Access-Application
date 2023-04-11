package com.rainc.firestorecore.tools

import android.annotation.SuppressLint
import androidx.annotation.NonNull
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.util.Util
import com.rainc.firebaseglobal.model.GenericError
import com.rainc.firebaseglobal.tools.FIREBASE_ENTITY_DOES_NOT_EXISTS
import com.rainc.firestorecore.extension.awaitScopeCancellation
import com.rainc.firestorecore.entity.BaseEntity
import com.rainc.firestorecore.entity.BaseManagedEntity
import com.rainc.firestorecore.entity.BaseSubCollectionEntity
import com.rainc.firestorecore.entity.BaseSubCollectionManagedEntity
import com.rainc.firestorecore.entity.SubCollection
import com.rainc.firestorecore.entity.user.User
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.time.Instant

class FirestoreCore internal constructor(private val firestore:FirebaseFirestore) {


    fun <T : BaseEntity> getCollectionName(@NonNull valueType: Class<T>): String {
        return when (valueType) {
            User::class.java -> "users"
            else -> throw UnsupportedOperationException("${valueType::class} is not supported")
        }
    }


    fun <T : BaseEntity> getEntity(id: String, @NonNull valueType: Class<T>): CompletableDeferred<Result<T>> {
        val result = CompletableDeferred<Result<T>>()
        firestore
            .collection(getCollectionName(valueType))
            .document(id)
            .get()
            .addOnCompleteListener {
                runCatching {
                    if(it.isSuccessful.not()) {
                        result.complete(Result.failure(it.exception?: Throwable(it.toString())))
                        return@addOnCompleteListener
                    }

                    val documentSnapshot = it.result
                    if (documentSnapshot.exists()) {
                       Result.success(documentSnapshot.toAppObject(valueType)!!)
                    } else {
                        Result.failure(
                            GenericError(
                                FIREBASE_ENTITY_DOES_NOT_EXISTS, "Could not find such entity with id $id"))
                    }
                }.onSuccess {
                    result.complete(it)
                }.onFailure { error ->
                    result.complete(Result.failure(it.exception?: error))
                }
            }
        return result
    }

    fun createOrUpdateEntity(entity: BaseSubCollectionManagedEntity): CompletableDeferred<Result<Unit>> {
        if (entity.documentId.isBlank()) {
            entity.documentId = generateId()
        }
        val completableDeferred = CompletableDeferred<Result<Unit>>()
        firestore
            .collection(getCollectionName(entity.subCollectionType().getParentCollectionClass()))
            .document(entity.parentDocumentId)
            .collection(entity.subCollectionType().collectionId)
            .document(entity.documentId)
            .set(entity, SetOptions.mergeFields(entity.mergeFields()))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    completableDeferred.complete(Unit.asResult())
                } else {
                    completableDeferred.complete(GenericError(error = it.exception?.localizedMessage).asResult())
                }
            }
        return completableDeferred
    }

    fun <T : BaseManagedEntity> createOrUpdateEntity(
        @NonNull valueType: Class<T>, entity: BaseManagedEntity,
        callback: (errorMessage: String?) -> Unit,
    ) {
        if (entity.documentId.isBlank()) {
            entity.documentId = generateId()
        }
        firestore
            .collection(getCollectionName(valueType))
            .document(entity.documentId)
            .set(entity, SetOptions.mergeFields(entity.mergeFields()))
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    callback.invoke(null)
                } else {
                    callback.invoke(it.exception?.localizedMessage ?: "Unable to save data.")
                }
            }
    }

    fun <T : BaseEntity> getEntityFlow(id: String, @NonNull valueType: Class<T>): Flow<Result<T>> = channelFlow {
        val listener = firestore
            .collection(getCollectionName(valueType))
            .document(id)
            .addSnapshotListener { documentSnapshot, error ->
                if (documentSnapshot != null) {
                    if (documentSnapshot.exists()) {
                        trySend(runCatching { documentSnapshot.toAppObject(valueType)!! })
                    } else {
                        trySend(
                            Result.failure(
                                GenericError(
                                    FIREBASE_ENTITY_DOES_NOT_EXISTS,
                                    "Could not find such entity with id $id")
                            )
                        )
                    }

                } else {
                    trySend(Result.failure(GenericError(error = error?.localizedMessage)))
                }
            }
        listener.awaitScopeCancellation()
    }

    fun <T : BaseEntity> getEntityListFlowWithQuery(query: CollectionReference.() -> Query, @NonNull valueType: Class<T>): Flow<Result<List<T>>> = channelFlow {
        val listener = firestore
            .collection(getCollectionName(valueType))
            .query()
            .addSnapshotListener { documentSnapshot, error ->
                if(error != null){
                    trySend(Result.failure(GenericError(error = error.localizedMessage)))
                    return@addSnapshotListener
                }
                if (documentSnapshot != null) {
                    val list = documentSnapshot.toAppObject(valueType)
                    trySend(Result.success(list))
                }
            }
        listener.awaitScopeCancellation()
    }

    fun <T : BaseEntity> getEntityListFlow(@NonNull valueType: Class<T>): Flow<Result<List<T>>> = channelFlow {
        val listener = firestore
            .collection(getCollectionName(valueType))
            .addSnapshotListener { documentSnapshot, error ->
                if(error != null){
                    trySend(Result.failure(GenericError(error = error.localizedMessage)))
                    return@addSnapshotListener
                }
                if (documentSnapshot != null) {
                    val list = documentSnapshot.toAppObject(valueType)
                    trySend(Result.success(list))
                }
            }
        listener.awaitScopeCancellation()
    }


    fun <T : BaseSubCollectionEntity> getEntityListFlow(
        @NonNull valueType: Class<T>,
        subCollection: SubCollection,
        query : CollectionReference.() ->Query,
        parentDocumentId: String): Flow<Result<List<T>>> = channelFlow {

        if (parentDocumentId.isNotBlank()) {
            val registration = firestore
                .collection(getCollectionName(subCollection.getParentCollectionClass()))
                .document(parentDocumentId)
                .collection(subCollection.collectionId)
                .query()
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        trySend(Result.failure(GenericError(error = error.localizedMessage)))
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        trySend(runCatching { snapshot.toAppObject(valueType) })
                    }
                }

           registration.awaitScopeCancellation()
        }
    }

    @SuppressLint("RestrictedApi")
    fun generateId(withTimeStamp: Boolean = false): String {
        return if (withTimeStamp) "${Instant.now().epochSecond}_${Util.autoId()}" else Util.autoId()
    }


    private fun <T : BaseEntity> QuerySnapshot.toAppObject(@NonNull valueType: Class<T>, parentDocumentId: String? = null): List<T> {
        val list = mutableListOf<T>()
        forEach {
            val appObject = it.toAppObject(valueType, parentDocumentId)
            if (appObject != null) {
                list.add(appObject)
            }
        }
        return list
    }

    /**
     * Here you can add custom parsing logic
     */
    @Suppress("UNCHECKED_CAST")
    private fun <T : BaseEntity> DocumentSnapshot.toAppObject(@NonNull valueType: Class<T>, parentDocumentId: String? = null): T? {
        return runCatching {
            val appObject = castObject (valueType)
            appObject.documentId = id

            if (parentDocumentId != null) {
                (appObject as BaseSubCollectionEntity).parentDocumentId = parentDocumentId
            }

            appObject

        }.getOrNull()
    }

    private fun <T : BaseEntity> DocumentSnapshot.castObject(valueType: Class<T>): T {
        return when (valueType) {
            else -> toObject(valueType)
        } as T
    }

    private inline fun <reified T, reified V> T.asResult():Result<V>{
       return if(this is V) Result.success(this) else {
            if(this is Throwable)  Result.failure<V>(this) else{
                Result.failure(ClassCastException("T is not V and is not Throwable"))
            }
        }
    }
}