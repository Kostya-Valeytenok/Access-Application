package com.rainc.repository

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

open class UseCase <R : Repository>  (protected val repository: R){

    protected suspend inline fun <T> doUseCase(crossinline block : suspend CoroutineScope.()->T) = withContext(
        Dispatchers.Default){
        return@withContext runCatching { this@withContext.block() }
    }
}