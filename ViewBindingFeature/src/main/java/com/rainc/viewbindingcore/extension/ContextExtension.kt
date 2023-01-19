package com.rainc.viewbindingcore.extension

import android.content.Context
import androidx.annotation.DimenRes
import kotlin.math.roundToInt

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