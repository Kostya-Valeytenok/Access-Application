package com.rainc.keyprovider.tools

import com.rainc.crypto.tools.KeyProvider
import javax.crypto.SecretKey

internal interface MergedKeyProvider:KeyProvider {
    fun with(key:String): SecretKey

    fun innerKey():SecretKey
}