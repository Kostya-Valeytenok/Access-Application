package com.rainc.crypto.tools

import com.rainc.base64tools.Base64Tools
import com.rainc.crypto.model.EncryptedMessageModel
import javax.crypto.Cipher

abstract class Encryption {

    abstract fun encrypt(text:String, provider: KeyProvider):Result<EncryptedMessageModel>
    abstract fun decrypt(messageModel: EncryptedMessageModel, provider: KeyProvider):Result<String>

    protected fun Cipher.encrypt(message:String): String {
        val encryptedMessage = doFinal(message.encodeToByteArray())
        return Base64Tools.encryptToString(encryptedMessage)
    }

    protected fun Cipher.decrypt(message: String): String {
        val encryptedMessage = Base64Tools.decrypt(message)!!
        return doFinal(encryptedMessage).decodeToString()
    }
}