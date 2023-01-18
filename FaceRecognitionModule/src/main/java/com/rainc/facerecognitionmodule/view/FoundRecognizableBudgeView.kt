package com.rainc.facerecognitionmodule.view

import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView
import androidx.core.animation.doOnRepeat
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import com.rainc.facerecognitionmodule.R

internal class FoundRecognizableBudgeView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SquareFrameLayout(context, attrs, defStyleAttr) {

    private val animator by lazy {
        ValueAnimator.ofInt(measuredWidth, (measuredWidth * 1.75).toInt()).apply {
            repeatMode = ValueAnimator.REVERSE
            repeatCount = 1
            duration = 250

            doOnRepeat {
                updateText()
            }

            addUpdateListener {
                updateLayoutParams {
                    width = it.animatedValue as Int
                }
            }
        }
    }

    private var textView: TextView
    var count: Int = 0

    init {
        inflate(context, R.layout.view_found_recognizable_budge_view, this)
        background = ContextCompat.getDrawable(context, R.drawable.background_found_recognizable_budge_view)

        textView = findViewById(R.id.budge_text)
        updateText()
    }

    fun add() {
        count++
        animator.cancel()
        animator.start()
    }

    fun updateText() {
        textView.text = "$count"
    }
}