package com.rainc.keyprovider.aes256GCM

import com.rainc.crypto.tools.CipherType
import com.rainc.keyprovider.inner.InnerKeyProvider
import com.rainc.keyprovider.tools.BaseMergedKeyProvider

internal class AESMergedKeyProvider internal constructor(innerKeyProvider: InnerKeyProvider): BaseMergedKeyProvider  (innerKeyProvider) {
    override val algorithm: CipherType
        get() = CipherType.AES_256_GCM
}