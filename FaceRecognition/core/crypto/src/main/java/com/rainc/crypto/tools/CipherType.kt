package com.rainc.crypto.tools

enum class CipherType(val raw:String) {

    AES_256_GCM("AES_256/GCM/NoPadding"),
    CHA_CHA_20_POLY1305("ChaCha20-Poly1305")
}