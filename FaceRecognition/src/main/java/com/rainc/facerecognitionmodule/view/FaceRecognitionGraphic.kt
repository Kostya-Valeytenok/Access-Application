package com.rainc.facerecognitionmodule.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.TypedValue
import com.rainc.facerecognitionmodule.functions.BoxData

// Defines an overlay on which the boxes and text will be drawn.
internal class FaceRecognitionGraphic(context: Context, overlay: GraphicOverlay?) : GraphicOverlay.Graphic(overlay) {
    var boxData: BoxData? = null
        set(value) {
            field = value
            invalidate()
        }

    private val greenBoxPrint = Paint().apply {
        color = Color.GREEN
        style = Paint.Style.STROKE
        strokeWidth = context.convertDpToPixel(3)
    }

    private val errorBoxPrint = Paint().apply {
        color = Color.RED
        style = Paint.Style.STROKE
        strokeWidth = context.convertDpToPixel(3)
    }

    private val textPaint = Paint().apply {
        strokeWidth = 2.0f
        textSize = 32f
        color = Color.WHITE
    }

    private val rectF = RectF()

    override fun draw(canvas: Canvas) {
        val boxData = boxData ?: return
        val face = boxData.face

        val x = translateX(face.boundingBox.centerX().toFloat())
        val y = translateY(face.boundingBox.centerY().toFloat())

        val left = x - scale(face.boundingBox.width() / 2.0f)
        val top = y - scale(face.boundingBox.height() / 2.0f)
        val right = x + scale(face.boundingBox.width() / 2.0f)
        val bottom = y + scale(face.boundingBox.height() / 2.0f)

        rectF.set(left, top, right, bottom)

        when (boxData) {
            is BoxData.NewFaceData -> {
                canvas.drawRect(rectF, if (boxData.isValid) greenBoxPrint else errorBoxPrint)
            }
            is BoxData.RecognitionData -> {
                val user = boxData.user

                canvas.drawRect(rectF, if (user != null) greenBoxPrint else errorBoxPrint)

                if (user != null) {
                    canvas.drawText(user.displayName, rectF.centerX(), rectF.centerY(), textPaint)
                }
            }
        }
    }

    private fun Context.convertDpToPixel(dp: Int): Float
    {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics,
        )
    }
}

