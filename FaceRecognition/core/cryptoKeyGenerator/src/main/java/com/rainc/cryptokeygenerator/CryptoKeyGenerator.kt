package com.rainc.cryptokeygenerator

import com.rainc.random.KeyGenerator

class CryptoKeyGenerator internal constructor(private val generator: KeyGenerator) {
    suspend fun generate(size:Int): String {
        return generator.generate(
            size = size,
            passwordType = KeyGenerator.PasswordType.FullRegisterSymbolWithNumbersPassword)
    }
}