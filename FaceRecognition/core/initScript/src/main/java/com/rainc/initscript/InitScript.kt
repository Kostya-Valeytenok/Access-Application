package com.rainc.initscript

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

abstract class InitScript {

    protected abstract suspend fun script()

    suspend fun runStrip() = withContext(Dispatchers.Default){
        script()
    }
}