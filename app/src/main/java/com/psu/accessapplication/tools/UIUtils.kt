package com.psu.accessapplication.tools

import android.os.Handler
import android.os.Looper

object UIUtils {
    @JvmStatic
    fun runOnMainThread(runnable: Runnable) {
        //main looper will be null for unit tests
        if (Looper.getMainLooper() != null && Thread.currentThread() !== Looper.getMainLooper().thread) {
            Handler(Looper.getMainLooper()).post(runnable)
        } else {
            runnable.run()
        }
    }
}