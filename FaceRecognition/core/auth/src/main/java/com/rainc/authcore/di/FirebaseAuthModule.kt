package com.rainc.authcore.di

import com.google.firebase.auth.FirebaseAuth
import com.rainc.authcore.tools.FirebaseAuthenticationManager
import org.koin.dsl.module

val firebaseAuthModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseAuthenticationManager(firebaseAuth = get()) }
}