package com.rainc.recognitionsource

import com.rainc.authcore.model.AuthUser
import com.rainc.authcore.tools.FirebaseAuthenticationManager
import com.rainc.coroutinecore.extension.updateUI
import com.rainc.crypto.model.EncryptedMessageModel
import com.rainc.crypto.model.EncryptionParams
import com.rainc.crypto.tools.CipherType
import com.rainc.cryptoserialization.tools.CryptoSerializer.decrypt
import com.rainc.cryptoserialization.tools.CryptoSerializer.encrypt
import com.rainc.firestorecore.entity.SubCollection
import com.rainc.firestorecore.entity.user.User
import com.rainc.firestorecore.tools.FirestoreCore
import com.rainc.recognitionsource.model.PersonData
import com.rainc.repository.Repository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.stream.Collectors

class RecognitionSourceRepository(
    private val firestore: FirestoreCore,
    private val authManager: FirebaseAuthenticationManager
) : Repository() {

    private val defaultAlgorithm = CipherType.AES_256_GCM

    private val currentUserMutableFlow =
        MutableSharedFlow<String>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val users = getUsers().sharedFlowRepository()
    private val currentUserFlow: SharedFlow<String>
        get() = currentUserMutableFlow
    val isUserSignedIn: Boolean
        get() = authManager.isUserSignedIn
    val userId: String?
        get() = authManager.userId

    suspend fun logIn(){
        if(isUserSignedIn) return

        signInAnonymously()
        currentUserFlow.first()
    }
    suspend fun signInAnonymously(): Result<AuthUser> {
        return authManager.signInAnonymously().onSuccess {
            currentUserMutableFlow.emit(value = it.id)
        }.onFailure {
            signInAnonymously()
        }
    }

    suspend fun uploadPersonModel(model: PersonData) = request {
        val encryptedModel = withContext(Dispatchers.Default) {
            model.encrypt(algorithm = defaultAlgorithm).getOrThrow()
        }

        val newUser = User.newUser(
            id = firestore.generateId(),
            IV = encryptedModel.params.IV,
            ADD = encryptedModel.params.ADD,
            model = encryptedModel.message
        )

        createNewUserSuspend(user = newUser)

    }

    private suspend fun createNewUserSuspend(user: User): String? {
        val completableDeferred = CompletableDeferred<String?>()

        firestore.createOrUpdateEntity(User::class.java, user) {
            completableDeferred.complete(it)
        }

        return completableDeferred.await()
    }

    private fun getUsers(): Flow<List<Result<PersonData>>> =
        firestore.getEntityListFlow(User::class.java).map {

            it.getOrDefault(emptyList()).parallelStream().map {
                runCatching {
                    EncryptedMessageModel(
                        message = it.model,
                        params = EncryptionParams(
                            IV = it.IV,
                            ADD = it.ADD
                        )
                    ).decrypt<PersonData>(defaultAlgorithm).getOrThrow()
                }
            }.collect(Collectors.toList())
        }.flowOn(Dispatchers.Default)

}