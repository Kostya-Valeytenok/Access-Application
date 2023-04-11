package com.rainc.facerecognitionmodule.tools.mfra.data

import com.rainc.coroutinecore.tools.CoroutineWorker
import com.rainc.facerecognitionmodule.tools.mfra.model.Person
import kotlinx.coroutines.flow.MutableStateFlow

object PersonDataCache {

    val cache = MutableStateFlow(listOf<Person>())

    fun init(persons: List<Person>) {
        CoroutineWorker.launch { cache.emit(persons) }
    }

    fun clean() {
        CoroutineWorker.launch { cache.emit(emptyList()) }
    }
}
