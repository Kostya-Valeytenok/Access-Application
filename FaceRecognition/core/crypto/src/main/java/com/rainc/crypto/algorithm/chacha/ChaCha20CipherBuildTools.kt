package com.rainc.crypto.algorithm.chacha

import com.rainc.base64tools.Base64Tools
import com.rainc.crypto.model.EncryptionParams
import com.rainc.crypto.tools.CipherType
import com.rainc.crypto.tools.EncryptionMode
import com.rainc.crypto.tools.EncryptionCipherBuildTools
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec

internal class ChaCha20CipherBuildTools : EncryptionCipherBuildTools() {

    private val NONCE_LEN = 12
    override val cipherType: CipherType
        get() = CipherType.CHA_CHA_20_POLY1305

    override fun getInputParams(): EncryptionParams {
        return  EncryptionParams(
            IV = Base64Tools.encryptToString(SecureRandom().generateSeed(NONCE_LEN)),
            ADD = Base64Tools.encryptToString(ByteArray(0))
        )
    }

    override fun initializeCipher(
        encryptionMode: EncryptionMode,
        params: EncryptionParams,
        key: SecretKey
    ): Cipher {

        val CHACHA20 = Cipher.getInstance(cipherType.raw)

        val ivSpec = IvParameterSpec(Base64Tools.decrypt(params.IV)!!)

        val mode = when(encryptionMode){
            EncryptionMode.ENCRYPT_MODE ->Cipher.ENCRYPT_MODE
            EncryptionMode.DECRYPT_MODE -> Cipher.DECRYPT_MODE
        }

        CHACHA20.init(mode, key, ivSpec)

        return CHACHA20
    }


}