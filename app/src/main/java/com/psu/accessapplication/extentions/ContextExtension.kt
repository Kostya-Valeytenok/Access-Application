package com.psu.accessapplication.extentions

import android.app.Dialog
import android.content.Context
import android.util.DisplayMetrics
import android.view.WindowManager
import com.psu.accessapplication.R

val Context.displayMetrics: DisplayMetrics
    get()
    {
        val windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        return displayMetrics
    }

fun Context.createProgressDialog(): Dialog {
    return Dialog(this).also {
        it.setContentView(R.layout.dialog_progress)
        it.setCancelable(false)
        // it.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        it.window?.setBackgroundDrawableResource(android.R.color.transparent)
        it.window?.attributes = it.window?.attributes?.apply {
            dimAmount=0.8f
        }
    }
}