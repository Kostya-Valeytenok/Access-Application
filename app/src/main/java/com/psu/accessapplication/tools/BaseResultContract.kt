package com.psu.accessapplication.tools

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.FragmentActivity

abstract class BaseResultContract<Input, Result : Parcelable>(private val clazz: Class<out FragmentActivity>) : ActivityResultContract<Input, Result>() {
    companion object {
        const val KEY_INPUT = "CONTRACT_INPUT"
        const val KEY_RESULT = "CONTRACT_RESULT"
    }

    override fun createIntent(context: Context, input: Input): Intent {
        val intent = Intent(context, clazz)
        if (input is Parcelable) {
            intent.putExtra(KEY_INPUT, input)
        }
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Result? {
        return intent?.getParcelableExtra(KEY_RESULT)
    }

}