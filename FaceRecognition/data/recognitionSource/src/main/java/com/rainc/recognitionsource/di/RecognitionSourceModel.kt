package com.rainc.recognitionsource.di

import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.rainc.authcore.di.firebaseAuthModule
import com.rainc.crypto.di.ChaCha20EncryptionModule
import com.rainc.cryptoserialization.di.cryptoSerializerModule
import com.rainc.firestorecore.di.firestoreModule
import com.rainc.recognitionsource.RecognitionSourceRepository
import com.rainc.recognitionsource.tools.ImageTransformManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val recognitionSourceModel = module {

    includes(firestoreModule)
    includes(firebaseAuthModule)
    includes(cryptoSerializerModule)

    single {
        RecognitionSourceRepository(
            firestore = get(),
            authManager = get()
        )
    }
    single {
        ImageTransformManager(
            imageLoader = get()
        )
    }
    single { Glide.with(androidContext()) }
    single { Gson() }
}