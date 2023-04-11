package com.rainc.crypto.di

import com.rainc.crypto.algorithm.aes.AES256GCMCipherBuildTools
import com.rainc.crypto.algorithm.aes.AESEncryption
import com.rainc.crypto.algorithm.chacha.ChaCha20CipherBuildTools
import com.rainc.crypto.algorithm.chacha.ChaCha20Encryption
import org.koin.dsl.module

val AES256GCMEncryptionModule = module{
    single { AES256GCMCipherBuildTools() }
    single { AESEncryption(cipherBuilder = get()) }
}