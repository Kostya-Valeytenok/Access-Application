package com.rainc.facerecognitionmodule.extentions

import android.content.Context
import android.os.Handler
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.AnyThread
import androidx.annotation.DimenRes
import kotlin.math.roundToInt

val Context.displayMetrics: DisplayMetrics
    get()
    {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics
    }

@AnyThread
fun Context.showToast(text: String, duration: Int = Toast.LENGTH_LONG) {
    Handler(mainLooper).post {
        Toast.makeText(this, text, duration).show()
    }
}