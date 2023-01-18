package com.psu.accessapplication.extentions

import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager

val Context.displayMetrics: DisplayMetrics
    get()
    {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics
    }