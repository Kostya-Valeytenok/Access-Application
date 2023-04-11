package com.rainc.keyprovider.chacha20

import com.rainc.crypto.tools.CipherType
import com.rainc.keyprovider.inner.InnerKeyProvider
import com.rainc.keyprovider.tools.BaseMergedKeyProvider

internal class ChaCha20MergedKeyProvider internal constructor(innerKeyProvider: InnerKeyProvider): BaseMergedKeyProvider  (innerKeyProvider) {
    override val algorithm: CipherType
        get() = CipherType.CHA_CHA_20_POLY1305
}