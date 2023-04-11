package com.rainc.crypto.model

data class EncryptedMessageModel(
    val message: String,
    val params: EncryptionParams
)