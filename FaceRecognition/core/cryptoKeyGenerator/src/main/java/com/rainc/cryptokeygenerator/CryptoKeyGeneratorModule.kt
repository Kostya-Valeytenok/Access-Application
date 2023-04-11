package com.rainc.cryptokeygenerator

import com.rainc.random.randomModule
import org.koin.dsl.module

val cryptoKeyGeneratorModule = module {
    includes(randomModule)
    single { CryptoKeyGenerator(generator = get()) }
}