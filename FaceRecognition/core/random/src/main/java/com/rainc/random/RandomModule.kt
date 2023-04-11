package com.rainc.random

import com.rainc.random.KeyGenerator
import com.rainc.random.PRNGFixes
import com.rainc.random.RandomCore
import org.koin.dsl.module

val randomModule = module {
    single { PRNGFixes.LinuxPRNGSecureRandom() }
    single { RandomCore(random = get()) }
    single { KeyGenerator(randomCore = get()) }
}