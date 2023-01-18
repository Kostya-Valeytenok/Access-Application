package com.psu.accessapplication.di.modules

import androidx.room.Room
import com.psu.accessapplication.repository.AppDatabase
import com.psu.accessapplication.tools.DownloadManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database")
        .fallbackToDestructiveMigration().build()
    }
    single { DownloadManager(imageLoader = get()) }
}