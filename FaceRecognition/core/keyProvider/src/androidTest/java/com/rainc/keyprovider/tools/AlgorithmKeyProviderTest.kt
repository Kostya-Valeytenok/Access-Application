package com.rainc.keyprovider.tools

import com.rainc.crypto.algorithm.chacha.ChaCha20Encryption
import com.rainc.crypto.di.AES256GCMEncryptionModule
import com.rainc.crypto.di.ChaCha20EncryptionModule
import com.rainc.crypto.tools.EncryptionAlgorithm
import com.rainc.keyprovider.di.algorithmKeyProviderModule
import org.junit.After
import org.junit.Assert
import org.junit.Before

import org.junit.Test
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class AlgorithmKeyProviderTest: KoinComponent {


    @Before
    fun initDI(){
        startKoin {
            modules(algorithmKeyProviderModule, AES256GCMEncryptionModule, ChaCha20EncryptionModule)
        }
    }
    
    @Test
    fun forAlgorithm_aes() {
        val testMessage = "it is test message"
        val protectedKey = "WTFoeXJZa090bEpFT2NqZQ=="
        println(protectedKey.length)
        val provider = get<AlgorithmKeyProvider>()
        val aes:EncryptionAlgorithm = get<ChaCha20Encryption>()
        val aesKeyProvider = provider.forAlgorithm(cipher = aes.algorithm).withProtectedKey(protectedKey)
        val encryptedMessage = aes.encrypt(
            text = testMessage,
            provider = aesKeyProvider).getOrThrow()

        println(encryptedMessage.message)

        Assert.assertNotEquals(testMessage, encryptedMessage.message)

        Assert.assertEquals(testMessage,aes.decrypt(messageModel = encryptedMessage, provider = aesKeyProvider).getOrThrow())
    }

    @Test
    fun forAlgorithm_chacha() {
        val testMessage = "it is test message"
        val protectedKey = "WTFoeXJZa090bEpFT2NqZQ=="
        println(protectedKey.length)
        val provider = get<AlgorithmKeyProvider>()
        val aes:EncryptionAlgorithm = get<ChaCha20Encryption>()
        val aesKeyProvider = provider.forAlgorithm(cipher = aes.algorithm).withProtectedKey(protectedKey)
        val encryptedMessage = aes.encrypt(
            text = testMessage,
            provider = aesKeyProvider).getOrThrow()

        println(encryptedMessage.message)

        Assert.assertNotEquals(testMessage, encryptedMessage.message)

        Assert.assertEquals(testMessage,aes.decrypt(messageModel = encryptedMessage, provider = aesKeyProvider).getOrThrow())
    }


    @After
    fun release(){
        stopKoin()
    }
}