package com.rainc.keyprovider.tools

internal fun ByteArray.marge(another:ByteArray): ByteArray {
    require(this.size == another.size) { "${this.size} != ${another.size}" }
    val array = ByteArray(size+another.size)
    var i = 0
    forEach {  byte ->
        array[i] = byte
        i+=2
    }
    i = 1
    another.forEach {  byte ->
        array[i] = byte
        i+=2
    }
    return array
}