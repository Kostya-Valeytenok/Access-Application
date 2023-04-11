package com.rainc.facerecognitionmodule.di

import com.rainc.facerecognitionmodule.tools.InitDefaultFacesDataScript
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val faceRecognitionInitScriptModule = module {
    single { InitDefaultFacesDataScript(context = androidContext()) }
}