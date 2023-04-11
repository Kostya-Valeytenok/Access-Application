package com.rainc.cryptoserialization.tools

import com.rainc.crypto.algorithm.aes.AESEncryption
import com.rainc.crypto.algorithm.chacha.ChaCha20Encryption
import com.rainc.crypto.model.EncryptedMessageModel
import com.rainc.crypto.tools.CipherType
import com.rainc.cryptoserialization.model.EncryptedModel
import com.rainc.keyprovider.tools.AlgorithmKeyProvider
import com.rainc.serialization.Serializer.decode
import com.rainc.serialization.Serializer.serialize

 class InternalCryptoSerializer internal constructor (
    val keyProvider: AlgorithmKeyProvider,
    val chaCha20Encryption: ChaCha20Encryption,
    val aes256GCMEncryption: AESEncryption,
    ) {
    inline fun <reified T: EncryptedModel> T.encryptModel(algorithm: CipherType) : Result<EncryptedMessageModel> = runCatching {
        val serializedModel = this.serialize()
        val keyProvider = keyProvider.forAlgorithm(cipher =algorithm)


        val algorithmEncoder = when(algorithm){
            CipherType.CHA_CHA_20_POLY1305 -> chaCha20Encryption
            CipherType.AES_256_GCM -> aes256GCMEncryption
        }

        algorithmEncoder.encrypt(
            text = serializedModel,
            provider = keyProvider.innerKeyProvider()).getOrThrow()
    }

    inline fun <reified T: EncryptedModel> EncryptedMessageModel.decryptModel(algorithm: CipherType) : Result<T> = runCatching {

        val keyProvider = keyProvider.forAlgorithm(cipher =algorithm)

        val algorithmEncoder = when(algorithm){
            CipherType.CHA_CHA_20_POLY1305 -> chaCha20Encryption
            CipherType.AES_256_GCM -> aes256GCMEncryption
        }

        val serializedModel = algorithmEncoder.decrypt(
            messageModel = this,
            provider = keyProvider.innerKeyProvider()).getOrThrow()

        serializedModel.decode()
    }
}