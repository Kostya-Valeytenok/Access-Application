package com.rainc.authcore.tools

import com.rainc.authcore.model.AuthUser

interface AuthenticationInterface {
    val currentUser: AuthUser?
    val isUserSignedIn: Boolean
    val userId: String?
    val isAnonymous: Boolean?

    suspend fun createUser(email: String, password: String): Result<AuthUser>

    suspend fun signInWithEmail(email: String, password: String): Result<AuthUser>
    suspend fun signInAnonymously(): Result<AuthUser>
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun signOut()
    fun deleteUser()
}


