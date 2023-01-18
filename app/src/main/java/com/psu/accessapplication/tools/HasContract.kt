package com.psu.accessapplication.tools

import android.app.Activity
import android.content.Intent
import android.os.Parcelable
import androidx.fragment.app.FragmentActivity

const val KEY_INPUT = "CONTRACT_INPUT"
const val KEY_RESULT = "CONTRACT_RESULT"

interface HasContract<Input : Parcelable, Result : Parcelable> {
    val contractInput: Input
        get() {
            val fragmentActivity = this as FragmentActivity
            return fragmentActivity.intent.getParcelableExtra(KEY_INPUT)!!
        }

    fun setContractResult(result: Result) {
        val fragmentActivity = this as FragmentActivity
        fragmentActivity.setResult(Activity.RESULT_OK, Intent().putExtra(KEY_RESULT, result))
        fragmentActivity.finish()
    }
}

interface HasContractNullable<Input : Parcelable, Result : Parcelable?> {
    val contractInput: Input
        get() {
            val fragmentActivity = this as FragmentActivity
            return fragmentActivity.intent.getParcelableExtra(KEY_INPUT)!!
        }

    fun setContractResult(result: Result?) {
        val fragmentActivity = this as FragmentActivity
        fragmentActivity.setResult(Activity.RESULT_OK, Intent().putExtra(KEY_RESULT, result))
        fragmentActivity.finish()
    }
}