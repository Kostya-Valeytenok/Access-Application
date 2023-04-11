package com.rainc.serialization

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
object Serializer {

    inline fun <reified T> T.serialize():String{
       return Json.encodeToString(this)
    }

    inline fun <reified T> String.decode():T{
        return Json.decodeFromString(this)
    }
}