package com.rainc.authcore.tools

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.firebase.auth.FirebaseUser
import com.rainc.authcore.extension.toResult
import com.rainc.authcore.model.AuthUser
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.channelFlow

open class FirebaseAuthenticationManager internal constructor(private val firebaseAuth: FirebaseAuth) : AuthenticationInterface {
    val firebaseUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override val isAnonymous: Boolean?
        get() = firebaseUser?.isAnonymous
    override val isUserSignedIn: Boolean
        get() = firebaseUser != null
    override val userId: String?
        get() = firebaseAuth.currentUser?.uid
    override val currentUser: AuthUser?
        get() = if (firebaseUser != null) AuthUser(firebaseUser!!) else null

    override suspend fun createUser(email: String, password: String): Result<AuthUser> {
        val completableDeferred = CompletableDeferred<Result<AuthUser>>()

        val taskListener: (Task<AuthResult>) -> Unit = { task ->
            val resultWrapper = if (task.isSuccessful) {
                Result.success(AuthUser(firebaseUser!!))
            } else {
                Result.failure(exception = task.exception?:Throwable())
            }

            completableDeferred.complete(resultWrapper)
        }

        if (firebaseUser != null && isAnonymous == true) {
            val credentials = EmailAuthProvider.getCredential(email, password)
            firebaseUser!!.linkWithCredential(credentials).addOnCompleteListener(taskListener)
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(taskListener)
        }

        return completableDeferred.await()
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<AuthUser> {
        val defaultCompletableDeferred = CompletableDeferred<Result<AuthUser>>()
        defaultCompletableDeferred.complete(firebaseAuth.signInWithEmailAndPassword(email, password).toResult().map { AuthUser(it.user!!) })
        return defaultCompletableDeferred.await()
    }

    override suspend fun signInAnonymously(): Result<AuthUser> {
        val completableDeferred = CompletableDeferred<Result<AuthUser>>()

        completableDeferred.complete(firebaseAuth.signInAnonymously().toResult().map { AuthUser(it.user!!) })

        return completableDeferred.await()
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        val resultWrapper = firebaseAuth.sendPasswordResetEmail(email).toResult()
        return resultWrapper.fold(
            onSuccess = { Result.success(Unit) },
            onFailure = {
                //GenericError(error = application.getString(R.string.error))
                Result.failure(it)
            }
        )
    }

    suspend fun withStateListener() = channelFlow {
        val stateListener = AuthStateListener{
            trySend(it)
        }
        firebaseAuth.addAuthStateListener (stateListener)
        runCatching { awaitCancellation() }
            .onFailure {
                when(it) {
                    is CancellationException -> firebaseAuth.removeAuthStateListener(stateListener)
                }
            }
    }

    override suspend fun signOut() {
      //  Repository.clearAllLocalData()
        firebaseAuth.signOut()
    }

    override fun deleteUser() {
        firebaseUser?.delete()
    }

}