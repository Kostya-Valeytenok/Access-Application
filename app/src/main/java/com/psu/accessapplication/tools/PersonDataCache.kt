package com.psu.accessapplication.tools

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
