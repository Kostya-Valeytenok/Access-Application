package com.rainc.cryptokeygenerator

import android.util.Base64
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin

class CryptoKeyGeneratorTest:KoinComponent {

    @Before
    fun init(){
        startKoin {
            modules(cryptoKeyGeneratorModule)
        }
    }
    @Test
    fun generate() {
        val generator = get<CryptoKeyGenerator>()
        val key = runBlocking { generator.generate(size =16) }
        val encodedString = Base64.encodeToString(key.toByteArray(), Base64.NO_WRAP)
        val decodedString =  Base64.decode(encodedString, Base64.NO_WRAP).decodeToString()
        println(key)
        println(encodedString)
        println(decodedString)
        assertEquals(key, decodedString)
        assertEquals(16,key.length)
    }
}