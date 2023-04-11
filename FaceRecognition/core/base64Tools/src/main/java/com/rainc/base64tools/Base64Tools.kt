package com.rainc.base64tools

import android.util.Base64

object Base64Tools {

    fun encryptToString(value: ByteArray):String{
        return Base64.encodeToString(value, Base64.NO_WRAP)
    }

    fun encryptToString(value: String):String{
        return Base64.encodeToString(value.encodeToByteArray(), Base64.NO_WRAP)
    }

    fun decrypt(value: String): ByteArray? {
       return Base64.decode(value, Base64.NO_WRAP)
    }
}