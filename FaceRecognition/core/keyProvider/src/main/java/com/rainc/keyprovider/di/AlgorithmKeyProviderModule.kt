package com.rainc.keyprovider.di

import com.rainc.crypto.tools.CipherType
import com.rainc.cryptokeygenerator.cryptoKeyGeneratorModule
import com.rainc.keyprovider.aes256GCM.AESMergedKeyProvider
import com.rainc.keyprovider.chacha20.ChaCha20MergedKeyProvider
import com.rainc.keyprovider.inner.InnerKeyProvider
import com.rainc.keyprovider.tools.AlgorithmKeyProvider
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val algorithmKeyProviderModule = module{
    includes(cryptoKeyGeneratorModule)
    single { AESMergedKeyProvider(innerKeyProvider = get { parametersOf(CipherType.AES_256_GCM) }) }
    single { ChaCha20MergedKeyProvider(innerKeyProvider = get { parametersOf(CipherType.CHA_CHA_20_POLY1305) }) }
    single { AlgorithmKeyProvider(
        aesMergedKeyProvider = get(),
        chaCha20MergedKeyProvider = get()
    ) }

    single { (param:CipherType) -> InnerKeyProvider(cipherType = param) }
}