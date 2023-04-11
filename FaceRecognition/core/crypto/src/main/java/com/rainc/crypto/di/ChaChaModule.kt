package com.rainc.crypto.di

import com.rainc.crypto.algorithm.chacha.ChaCha20CipherBuildTools
import com.rainc.crypto.algorithm.chacha.ChaCha20Encryption
import org.koin.dsl.module

val ChaCha20EncryptionModule = module{
    single { ChaCha20CipherBuildTools() }
    single { ChaCha20Encryption(cipherBuilder = get()) }
}