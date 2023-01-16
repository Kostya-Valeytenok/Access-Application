package com.psu.accessapplication.di

import android.app.Application
import android.content.Context
import com.psu.accessapplication.demo.functions.FaceRecognition
import com.psu.accessapplication.di.modules.appModule
import com.psu.accessapplication.di.modules.faceDetectorOptionsTypeModule
import com.psu.accessapplication.di.modules.faceRecognitionModule
import com.psu.accessapplication.di.modules.initializedModule
import com.psu.accessapplication.di.modules.vmModule
import com.psu.accessapplication.tools.AbstractPreferences
import com.psu.accessapplication.tools.initializer.Initializer
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
                initializedModule,
                appModule,
                vmModule,
                faceRecognitionModule,
                faceDetectorOptionsTypeModule))
        }

        Initializer.runInitScripts()
    }

    override fun attachBaseContext(base: Context) {
        AbstractPreferences.init(base)
        super.attachBaseContext(base)
    }
}
