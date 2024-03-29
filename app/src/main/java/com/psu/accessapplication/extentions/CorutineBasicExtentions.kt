package com.psu.accessapplication.extentions

import android.view.View
import com.sap.virtualcoop.mobileapp.helper.extension.gone
import com.sap.virtualcoop.mobileapp.helper.extension.visible
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

suspend fun <T> asyncJob(job: suspend () -> T): Deferred<T> = withContext(Dispatchers.Default) {
    return@withContext async { return@async job.invoke() }
}

suspend fun <T> launchJob(job: suspend () -> Unit) = withContext(Dispatchers.Default) {
    return@withContext async { return@async job.invoke() }
}

suspend fun updateUI(job: suspend () -> Unit) = withContext(Dispatchers.Main) {
    job.invoke()
}

suspend fun updateUISafe(job: suspend () -> Unit) = withContext(Dispatchers.Main) {
    runCatching { job.invoke() }
}

suspend inline fun <T> Flow<T>.collectOnce(crossinline collectAction: suspend (T) -> Unit): Job = withContext(Dispatchers.Default) {
    val job = Job()
    launch(job) {
        collect {
            collectAction.invoke(it)
            job.cancel()
        }
    }
    return@withContext job
}

suspend fun <T> Result<T>.updateUIIfSuccessFull(updateUIAction: (T) -> Unit): Result<T> {
    onSuccess { result -> updateUI { updateUIAction.invoke(result) } }
    return this
}

fun <T> T?.nullableToResult(): Result<T> {
    return if (this == null) Result.failure(NullPointerException())
    else Result.success(this)
}

suspend fun View.subscribeOnWorkingStatus(loadingStatus: MutableStateFlow<Boolean>) {
    loadingStatus.collect { isWorkInProcesses ->
        if (isWorkInProcesses) updateUI { this.visible() }
        else updateUI { this.gone() }
    }
}

