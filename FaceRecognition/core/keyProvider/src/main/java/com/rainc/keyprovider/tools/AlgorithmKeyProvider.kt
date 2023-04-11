package com.rainc.keyprovider.tools

import com.rainc.crypto.tools.CipherType
import com.rainc.crypto.tools.KeyProvider
import com.rainc.keyprovider.aes256GCM.AESMergedKeyProvider
import com.rainc.keyprovider.chacha20.ChaCha20MergedKeyProvider
import javax.crypto.SecretKey

class AlgorithmKeyProvider internal constructor(
    private val aesMergedKeyProvider: AESMergedKeyProvider,
    private val chaCha20MergedKeyProvider: ChaCha20MergedKeyProvider
) {

    fun forAlgorithm(cipher: CipherType): MergerKeyBuilder {
        val provider:MergedKeyProvider = when(cipher){
            CipherType.AES_256_GCM -> aesMergedKeyProvider
            CipherType.CHA_CHA_20_POLY1305 -> chaCha20MergedKeyProvider
        }

        return MergerKeyBuilder(provider)
    }
    class MergerKeyBuilder internal constructor (private val provider: MergedKeyProvider){
        fun withProtectedKey(key:String):KeyProvider{
            provider.with(key)
            return  provider
        }

        fun innerKeyProvider():KeyProvider = object : KeyProvider{
            override fun provideKey(): SecretKey {
                return provider.innerKey()
            }

        }
    }
}