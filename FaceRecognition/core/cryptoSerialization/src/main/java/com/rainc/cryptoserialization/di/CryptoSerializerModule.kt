package com.rainc.cryptoserialization.di

import com.rainc.crypto.di.AES256GCMEncryptionModule
import com.rainc.crypto.di.ChaCha20EncryptionModule
import com.rainc.cryptokeygenerator.cryptoKeyGeneratorModule
import com.rainc.cryptoserialization.tools.InternalCryptoSerializer
import com.rainc.keyprovider.di.algorithmKeyProviderModule
import org.koin.dsl.module

val cryptoSerializerModule = module {
    includes(cryptoKeyGeneratorModule)
    includes(algorithmKeyProviderModule)
    includes(ChaCha20EncryptionModule)
    includes(AES256GCMEncryptionModule)

    single {
        InternalCryptoSerializer(
            keyProvider = get(),
            chaCha20Encryption = get(),
            aes256GCMEncryption = get()
        )
    }
}