package com.psu.accessapplication.tools.initializer

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

object Initializer : KoinComponent {

    private val scripts = buildList<InitScript>{
        add(this@Initializer.get<InitDefaultFacesDataScript>())
    }

    fun runInitScripts(){
        runBlocking(Dispatchers.Default){
            scripts.map {
                launch { it.runStrip() }
            }.joinAll()
        }
    }
}