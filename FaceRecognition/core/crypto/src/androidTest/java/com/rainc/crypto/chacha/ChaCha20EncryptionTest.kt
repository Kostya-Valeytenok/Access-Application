package com.rainc.crypto.chacha

import com.rainc.crypto.algorithm.chacha.ChaCha20Encryption
import com.rainc.crypto.di.ChaCha20EncryptionModule
import com.rainc.crypto.tools.CipherType
import com.rainc.crypto.tools.EncryptionAlgorithm
import com.rainc.crypto.tools.KeyProvider
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

class ChaCha20EncryptionTest: KoinComponent {

    private val testKey ="2NtwpUWyV8r7crXPqScglDRwbfQYTEOqDUCK6SjvwXCQE3NikayFE6Xhgm9lctEVGg79o659O7h2x6y756Q9O5C1a5mjTVY921TEi91gg3YVPigZ7Bwh8q1P3G1V7GpZql4XY3uHWWTU1BiqrtHHdYeVJNcS1ffRiBrM5VNYuwRa5YYZq9AY5XFgUrGXozfxtVuYO8Z4hCwLxx1UunvpZyYclFmRyKjxAjrf3eA6vVxuQ5v6hL89J7tAtjMtXhnZ"
    val globalKeyProvider = object :KeyProvider{
        override fun provideKey(): SecretKey {

            testKey.mapNotNull {
               val size =  it.toString().toByteArray().size
                if(size>1) it else null
            }.forEach {
                println(it)
            }

            val key = testKey.toByteArray().take(32).toByteArray()

            println(testKey.length)
            println(key.size)

            return  SecretKeySpec(key, CipherType.CHA_CHA_20_POLY1305.raw)
        }

    }
    @Before
    fun initDI(){
        startKoin {
            modules(ChaCha20EncryptionModule)
        }
    }

    @Test
    fun encrypt() {
        val message = "This is test message"
        val chacha20: EncryptionAlgorithm = getKoin().get<ChaCha20Encryption>()

        val result = chacha20.encrypt(text = message, provider = globalKeyProvider)
            .onSuccess {
                println(it.message)
                assert(it.message.isNotBlank())
            }

       val encryptedMessage =  result.getOrThrow()

        Assert.assertEquals(message,chacha20.decrypt(
            messageModel = encryptedMessage,
            provider = globalKeyProvider).getOrThrow()
        )


    }

    @Test
    fun decrypt() {
    }

    @After
    fun release(){
        stopKoin()
    }
}