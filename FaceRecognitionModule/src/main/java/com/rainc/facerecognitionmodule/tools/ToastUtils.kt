package com.rainc.facerecognitionmodule.tools

import android.content.Context
import android.view.Gravity
import android.widget.Toast
import com.rainc.facerecognitionmodule.tools.UIUtils.runOnMainThread
import java.util.Date

object ToastUtils {
    private val lastDisplayTimeMap: MutableMap<String, Date> = HashMap()

    @JvmOverloads
    fun showToastOnUIThread(
        c: Context,
        message: String?,
        length: ToastDisplayLength? = ToastDisplayLength.LONG,
        key: String? = null,
        debounceIntervalInMillis: Long? = null
    ) {
        if (key != null && debounceIntervalInMillis != null) {
            val lastDisplayDate = lastDisplayTimeMap[key]
            val now = Date()
            if (lastDisplayDate != null && Date(lastDisplayDate.time + debounceIntervalInMillis).time > now.time) {
                return
            }
            lastDisplayTimeMap[key] = now
        }
        runOnMainThread {
            val toast = when (length) {
                ToastDisplayLength.SHORT -> Toast.makeText(c, message, Toast.LENGTH_SHORT)
                ToastDisplayLength.LONG -> Toast.makeText(c, message, Toast.LENGTH_LONG)
                else -> Toast.makeText(c, message, Toast.LENGTH_LONG)
            }
            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0)
            toast.show()
        }
    }

    enum class ToastDisplayLength {
        SHORT, LONG
    }
}