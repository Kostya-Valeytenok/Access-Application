package com.rainc.authcore.model

import com.google.firebase.auth.FirebaseUser

data class AuthUser(val id: String, val name: String?, val email: String?) {
    constructor(firebaseUser: FirebaseUser) : this(
        firebaseUser.uid,
        firebaseUser.displayName,
        firebaseUser.email
    )
}