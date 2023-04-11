package com.rainc.repository

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

open class Repository{
    protected suspend fun <T> request(
        context: CoroutineContext = Dispatchers.IO,
        block: suspend CoroutineScope.() -> T) = withContext(context) {
        async {
            runCatching { block.invoke(this) }
        }
    }


    protected fun Throwable.log() = {
        Log.e(this.toString(), this.localizedMessage?: "error", this)
    }

    protected fun <T> Flow<T>.sharedFlowRepository(): SharedFlow<T> {
        return flowOn(Dispatchers.Default).conflate().shareIn(GlobalScope, SharingStarted.Eagerly, 1)
    }
}