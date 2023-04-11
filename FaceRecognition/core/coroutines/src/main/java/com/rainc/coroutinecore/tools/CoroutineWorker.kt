package com.rainc.coroutinecore.tools

import android.util.Log
import kotlinx.coroutines.*

object CoroutineWorker {

    private var errorHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        Log.e(coroutineContext.toString(), throwable.message, throwable)
    }

    val scope = CoroutineScope(Dispatchers.Default + SupervisorJob() + errorHandler)

    fun getChild(): CoroutineScope {
        return CoroutineScope(scope.coroutineContext + Job(scope.coroutineContext.job))
    }

    fun launch(task: suspend () -> Unit): Job {
        return scope.launch { task.invoke() }
    }

    fun <T> asyncJob(task: suspend () -> T): Deferred<T> {
        return scope.async { return@async task.invoke() }
    }

    fun cancelAllJobs() {
        if (scope.isActive) {
            scope.coroutineContext.cancelChildren()
        }
    }
}
