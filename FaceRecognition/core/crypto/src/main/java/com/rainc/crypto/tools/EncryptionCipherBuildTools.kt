package com.rainc.crypto.tools

import com.rainc.crypto.model.EncryptionParams
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec

internal abstract class EncryptionCipherBuildTools {

    internal abstract val cipherType:CipherType

    abstract fun getInputParams(): EncryptionParams

    abstract fun initializeCipher(
        encryptionMode: EncryptionMode,
        params: EncryptionParams,
        key: SecretKey
    ):Cipher
}