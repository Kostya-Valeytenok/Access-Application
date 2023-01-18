package com.rainc.facerecognitionmodule.extentions

fun Map<String, Boolean>.isGranted(): Boolean {
    forEach {
        if (!it.value) {
            println(it.key + " failed")
            return false
        }
    }
    return true
}
