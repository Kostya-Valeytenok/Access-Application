package com.rainc.firestorecore.di

import com.google.firebase.firestore.FirebaseFirestore
import com.rainc.firestorecore.tools.FirestoreCore
import org.koin.dsl.module

val firestoreModule = module {
    single { FirebaseFirestore.getInstance() }
    single { FirestoreCore(firestore = get()) }
}