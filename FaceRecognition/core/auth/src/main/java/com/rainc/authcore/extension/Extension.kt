package com.rainc.authcore.extension

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CompletableDeferred

internal suspend fun <T> Task<T>.toResult(): Result<T> {
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