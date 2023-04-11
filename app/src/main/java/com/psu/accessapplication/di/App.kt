package com.psu.accessapplication.di

import android.app.Application
import android.content.Context
import com.psu.accessapplication.di.modules.appModule
import com.psu.accessapplication.di.modules.vmModule
import com.psu.accessapplication.tools.AbstractPreferences
import com.psu.accessapplication.tools.initializer.Initializer
import com.rainc.facerecognitionmodule.di.faceDetectorOptionsTypeModule
import com.rainc.facerecognitionmodule.di.faceRecognitionInitScriptModule
import com.rainc.facerecognitionmodule.di.faceRecognitionModule
import com.rainc.facerecognitionmodule.di.mfraModule
import com.rainc.facerecognitionmodule.functions.FaceRecognition
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    companion object {
        lateinit var application: Application
    }

    override fun onCreate() {
        super.onCreate()
        application = this
        FaceRecognition.init(this)
        startKoin {
            androidLogger()
            androidContext(applicationContext)

            modules(listOf(
                appModule,
                vmModule,
                faceRecognitionModule,
                faceDetectorOptionsTypeModule,
                faceRecognitionInitScriptModule,
                mfraModule)
            )
        }

        Initializer.runInitScripts()
    }

    override fun attachBaseContext(base: Context) {
        AbstractPreferences.init(base)
        super.attachBaseContext(base)
    }
}