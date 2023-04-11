package com.rainc.facerecognitionmodule.extentions

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet

fun View.hideKeyboard(context: Context?=null) {
    val imm = (context?:this.context).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)


}

inline fun View.invisible() {
    visibility = View.INVISIBLE
}

inline fun View.gone() {
    visibility = View.GONE
}

inline fun View.visible() {
    visibility = View.VISIBLE
}

fun ConstraintLayout.updateConstraints(builder: ConstraintSet.() -> Unit) {
    val constraintSet = ConstraintSet()
    constraintSet.clone(this)
    builder.invoke(constraintSet)
    constraintSet.applyTo(this)
}