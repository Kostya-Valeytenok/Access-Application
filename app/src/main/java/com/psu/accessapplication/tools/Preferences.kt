package com.psu.accessapplication.tools

import android.annotation.SuppressLint

@SuppressLint("StaticFieldLeak")
object Preferences : AbstractPreferences("APP_PREFERENCE") {
    var isFirstLaunch: Boolean by PreferencesDelegate("isFirstLaunch", true)
}
