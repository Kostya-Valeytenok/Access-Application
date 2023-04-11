package com.rainc.keyprovider.inner

import com.rainc.base64tools.Base64Tools
import com.rainc.crypto.tools.CipherType
import com.rainc.crypto.tools.KeyProvider
import com.rainc.keyprovider.BuildConfig
import com.rainc.keyprovider.tools.marge
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and
import kotlin.experimental.xor

internal class InnerKeyProvider(private val cipherType:CipherType) : KeyProvider {

    private val globalKey:String
        get() =  BuildConfig.SECURE_API_KEY

    private val byteKey:ByteArray

    init {
        val globalKey = Base64Tools.decrypt(globalKey)!!
        val reversed = globalKey.reversedArray()
        val firstKey =  ByteArray(16)
        val secondKey =  ByteArray(16)

        globalKey.forEachIndexed { index, byte ->
            firstKey[index] = globalKey[index].xor(byte)
        }

        firstKey.forEachIndexed { index, byte ->
            secondKey[index] = reversed[index].and(byte)
        }
        byteKey = secondKey.marge(firstKey)
    }
    override fun provideKey(): SecretKey = SecretKeySpec(byteKey,cipherType.raw)
}