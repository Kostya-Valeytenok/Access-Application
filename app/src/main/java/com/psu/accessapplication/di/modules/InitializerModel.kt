package com.psu.accessapplication.di.modules

import com.psu.accessapplication.tools.initializer.InitDefaultFacesDataScript
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val initializedModule = module {
    single { InitDefaultFacesDataScript(context = androidContext()) }
}