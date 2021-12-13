package com.psu.accessapplication.tools

import com.psu.accessapplication.model.Person

object PersonDataCache {

    val cache = mutableListOf<Person>()

    fun init(persons: List<Person>) {
        clean()
        cache.addAll(persons)
    }

    fun clean() {
        cache.clear()
    }
}
