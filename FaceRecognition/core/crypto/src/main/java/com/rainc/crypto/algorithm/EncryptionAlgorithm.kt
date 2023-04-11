package com.rainc.crypto.tools

import com.rainc.base64tools.Base64Tools
import com.rainc.crypto.model.EncryptedMessageModel
import javax.crypto.Cipher

abstract class EncryptionAlgorithm : Encryption() {

    abstract val algorithm: CipherType

}