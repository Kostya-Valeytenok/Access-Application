package com.rainc.crypto.tools

import javax.crypto.SecretKey

interface KeyProvider {

    fun provideKey():SecretKey
}