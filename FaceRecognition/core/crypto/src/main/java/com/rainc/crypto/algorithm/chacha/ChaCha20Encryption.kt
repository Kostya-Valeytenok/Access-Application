package com.rainc.crypto.algorithm.chacha

import com.rainc.crypto.model.EncryptedMessageModel
import com.rainc.crypto.tools.CipherType
import com.rainc.crypto.tools.EncryptionAlgorithm
import com.rainc.crypto.tools.EncryptionMode
import com.rainc.crypto.tools.KeyProvider

class ChaCha20Encryption internal constructor (private val cipherBuilder: ChaCha20CipherBuildTools) : EncryptionAlgorithm() {
     override val algorithm: CipherType
          get() = cipherBuilder.cipherType

     override fun encrypt(text:String, provider: KeyProvider): Result<EncryptedMessageModel> = runCatching{
          val newParams = cipherBuilder.getInputParams()
          val chaCha20 = cipherBuilder.initializeCipher(
               encryptionMode = EncryptionMode.ENCRYPT_MODE,
               params = newParams,
               key = provider.provideKey())

           EncryptedMessageModel(
                message = chaCha20.encrypt(message = text),
                params = newParams
          )
     }

     override fun decrypt(
          messageModel: EncryptedMessageModel,
          provider: KeyProvider
     ): Result<String> = runCatching{

          val chaCha20 = cipherBuilder.initializeCipher(
               encryptionMode = EncryptionMode.DECRYPT_MODE,
               params = messageModel.params,
               key = provider.provideKey())

          chaCha20.decrypt(message = messageModel.message)
     }

}