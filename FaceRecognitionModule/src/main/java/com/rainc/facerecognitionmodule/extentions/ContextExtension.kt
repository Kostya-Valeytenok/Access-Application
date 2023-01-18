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


fun Context.convertPxToDp(px: Int): Int {
    return (px / resources.displayMetrics.density).roundToInt()
}

fun Context.convertDpToPixel(dp: Int): Int {
    return (dp * resources.displayMetrics.density).roundToInt()
}

fun Context.convertSpToPixel(sp: Int): Int {
    return (sp * resources.displayMetrics.scaledDensity).roundToInt()
}

fun Context.getDimension(@DimenRes dimen: Int): Float {
    return resources.getDimension(dimen)
}

@AnyThread
fun Context.showToast(text: String, duration: Int = Toast.LENGTH_LONG) {
    Handler(mainLooper).post {
        Toast.makeText(this, text, duration).show()
    }
}