@file:Suppress("NOTHING_TO_INLINE")

package com.sap.virtualcoop.mobileapp.helper.extension

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.annotation.Px
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.*

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

inline fun View.updateMargin(
    @Px left: Int = marginLeft,
    @Px top: Int = marginTop,
    @Px right: Int = marginRight,
    @Px bottom: Int = marginBottom
) {
    updateLayoutParams {
        if (this is ViewGroup.MarginLayoutParams) {
            setMargins(left, top, right, bottom)
        }
    }
}

fun View.hideKeyboard(context: Context?=null) {
    val imm = (context?:this.context).getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}