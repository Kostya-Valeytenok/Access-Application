package com.rainc.keyprovider.tools

import com.rainc.base64tools.Base64Tools
import com.rainc.crypto.tools.CipherType
import com.rainc.keyprovider.BuildConfig
import com.rainc.keyprovider.inner.InnerKeyProvider
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.xor

internal abstract class  BaseMergedKeyProvider(private val innerKeyProvider: InnerKeyProvider) : MergedKeyProvider {

    abstract val algorithm: CipherType

    private val globalKey:String
        get() =  BuildConfig.SECURE_API_KEY

    var byteKey = ByteArray(32)
    override fun with(key:String): SecretKey {
        val secureKey = Base64Tools.decrypt(key)!!
        val globalKey = Base64Tools.decrypt(globalKey)!!

        secureKey.forEachIndexed { index, byte ->
            globalKey[index] = globalKey[index].xor(byte)
        }

        byteKey = secureKey.marge(globalKey)

        println(secureKey.joinToString())
        println(globalKey.joinToString())
        println(byteKey.joinToString())

        return provideKey()
    }
    override fun provideKey(): SecretKey {
       return SecretKeySpec(byteKey,algorithm.raw)
    }

    override fun innerKey():SecretKey = innerKeyProvider.provideKey()
}