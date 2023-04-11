package com.rainc.firestorecore

import com.google.android.gms.tasks.Task
import com.google.firebase.Timestamp
import kotlinx.coroutines.CompletableDeferred
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

suspend fun <T> Task<T>.toResult(): Result<T> {
    return CompletableDeferred<Result<T>>().apply {
        addOnCompleteListener { task ->
            val result  = if (task.isSuccessful) {
                Result.success(task.result)
            } else {
                Result.failure(task.exception?:Throwable())
            }
            complete(result)
        }
    }.await()
}

internal fun Timestamp.toLocaleDate() =  LocalDateTime.ofInstant(
    this.toDate().toInstant(),
    ZoneId.systemDefault()
)

internal fun Double.toLocaleDate() =  LocalDateTime.ofInstant(
    Instant.ofEpochSecond(this.toLong()),
    ZoneId.systemDefault()
)