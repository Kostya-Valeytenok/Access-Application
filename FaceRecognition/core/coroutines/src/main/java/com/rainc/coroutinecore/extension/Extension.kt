package com.rainc.coroutinecore.extension

import android.util.Log
import android.view.View
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

fun Fragment.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
) = viewLifecycleOwner.launch(context, start, block)

fun LifecycleOwner.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
) = lifecycleScope.launch(context, start, block)

internal val NoCrashCoroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
    println("failed")
    Log.e(
        throwable.cause.toString(),
        throwable.message,
        throwable
    )
}

fun View.doWhileAttached(block: suspend CoroutineScope.() -> Unit) {
    val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    doOnAttach {
        coroutineScope.launch(NoCrashCoroutineExceptionHandler, block = block)
        doOnDetach {
            coroutineScope.cancel()
        }
    }
}

suspend fun <T> asyncJob(job: suspend () -> T): Deferred<T> = withContext(Dispatchers.Default) {
    return@withContext async { return@async job.invoke() }
}

suspend fun <T> launchJob(job: suspend () -> Unit) = withContext(Dispatchers.Default) {
    return@withContext async { return@async job.invoke() }
}

suspend fun updateUI(block: suspend () -> Unit) = withContext(Dispatchers.Main) {
    block.invoke()
}

suspend fun updateUISafe(job: suspend () -> Unit) = withContext(Dispatchers.Main) {
    runCatching { job.invoke() }
}

suspend fun repeatParallel(times:Int, block:suspend ()->Unit) = withContext(Dispatchers.Default){
    repeat(times) {
        launch { block.invoke() }
    }
}

suspend fun <T> repeatParallelWithResult(times:Int, block:suspend ()->T) = withContext(Dispatchers.Default){
    return@withContext buildList {
        repeat(times) {
            add(async { block.invoke() })
        }
    }.awaitAll()
}