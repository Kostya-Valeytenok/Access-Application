package com.rainc.crypto.algorithm.aes

import com.rainc.base64tools.Base64Tools
import com.rainc.crypto.model.EncryptedMessageModel
import com.rainc.crypto.tools.CipherType
import com.rainc.crypto.tools.EncryptionAlgorithm
import com.rainc.crypto.tools.EncryptionMode
import com.rainc.crypto.tools.KeyProvider

class AESEncryption internal constructor(private val cipherBuilder: AES256GCMCipherBuildTools) : EncryptionAlgorithm() {

     override val algorithm: CipherType
          get() = cipherBuilder.cipherType
     override fun encrypt(text:String, provider: KeyProvider): Result<EncryptedMessageModel> = runCatching{
          val newParams = cipherBuilder.getInputParams()
          val aeS256GCM = cipherBuilder.initializeCipher(
               encryptionMode = EncryptionMode.ENCRYPT_MODE,
               params = newParams,
               key = provider.provideKey())

          val encryptedMessage = aeS256GCM.doFinal(text.encodeToByteArray())
          val result = Base64Tools.encryptToString(encryptedMessage)

           EncryptedMessageModel(
                message = result,
                params = newParams
          )
     }

     override fun decrypt(
          messageModel: EncryptedMessageModel,
          provider: KeyProvider
     ): Result<String> = runCatching{

          val aeS256GCM = cipherBuilder.initializeCipher(
               encryptionMode = EncryptionMode.DECRYPT_MODE,
               params = messageModel.params,
               key = provider.provideKey())

          val encryptedMessage = Base64Tools.decrypt(messageModel.message)!!

          aeS256GCM.doFinal(encryptedMessage).decodeToString()
     }

}