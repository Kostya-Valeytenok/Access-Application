package com.rainc.firestorecore.extension

import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.awaitCancellation
internal suspend fun ListenerRegistration.awaitScopeCancellation(){
    runCatching { awaitCancellation() }
        .onFailure {
            when(it) {
                is CancellationException ->this.remove()
            }
        }
}