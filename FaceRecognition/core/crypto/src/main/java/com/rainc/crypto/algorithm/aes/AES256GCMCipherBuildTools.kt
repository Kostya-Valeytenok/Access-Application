package com.rainc.crypto.algorithm.aes

import com.rainc.base64tools.Base64Tools
import com.rainc.crypto.model.EncryptionParams
import com.rainc.crypto.tools.CipherType
import com.rainc.crypto.tools.EncryptionMode
import com.rainc.crypto.tools.EncryptionCipherBuildTools
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

internal class AES256GCMCipherBuildTools : EncryptionCipherBuildTools() {

    val TAG_LENGTH = 16
    private val AAD_LENGTH = 16
    private val IV_LENGTH = 12
    override val cipherType: CipherType
        get() = CipherType.AES_256_GCM

    override fun getInputParams(): EncryptionParams {
        return  EncryptionParams(
            IV =  Base64Tools.encryptToString(SecureRandom().generateSeed(IV_LENGTH)),
            ADD = Base64Tools.encryptToString(SecureRandom().generateSeed(AAD_LENGTH))
        )
    }

    override fun initializeCipher(
        encryptionMode: EncryptionMode,
        params: EncryptionParams,
        key: SecretKey
    ): Cipher {

        val AES256GCM = Cipher.getInstance(cipherType.raw)
        val GCMSpec = GCMParameterSpec(TAG_LENGTH  * 8, Base64Tools.decrypt(params.IV))

        val mode = when(encryptionMode){
            EncryptionMode.ENCRYPT_MODE ->Cipher.ENCRYPT_MODE
            EncryptionMode.DECRYPT_MODE -> Cipher.DECRYPT_MODE
        }

        AES256GCM.init(mode, key, GCMSpec)
        AES256GCM.updateAAD(Base64Tools.decrypt(params.ADD))

        return AES256GCM
    }


}