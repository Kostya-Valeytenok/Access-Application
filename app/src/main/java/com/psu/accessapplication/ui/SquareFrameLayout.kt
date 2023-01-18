package com.psu.accessapplication.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import kotlin.math.max

open class SquareFrameLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {
    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)

        if (widthMode != View.MeasureSpec.EXACTLY) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
            val newMeasureSpec = MeasureSpec.makeMeasureSpec(max(measuredWidth, measuredHeight), MeasureSpec.EXACTLY)
            super.onMeasure(newMeasureSpec, newMeasureSpec)
        } else {
            super.onMeasure(widthMeasureSpec, widthMeasureSpec)
        }
    }
}