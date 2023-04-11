package com.rainc.cryptoserialization.tools

import com.rainc.crypto.model.EncryptedMessageModel
import com.rainc.crypto.tools.CipherType
import com.rainc.cryptoserialization.model.EncryptedModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object CryptoSerializer : KoinComponent  {

    val serializer: InternalCryptoSerializer by inject()
    inline fun <reified T: EncryptedModel> T.encrypt(algorithm: CipherType) : Result<EncryptedMessageModel> = runCatching {
        with(serializer){
            this@encrypt.encryptModel(algorithm = algorithm).getOrThrow()
        }
    }

    inline fun <reified T: EncryptedModel> EncryptedMessageModel.decrypt(algorithm: CipherType) : Result<T> = runCatching {
        with(serializer){
            this@decrypt.decryptModel<T>(algorithm = algorithm).getOrThrow()
        }
    }
}