package com.rainc.facerecognitionmodule.extentions

import android.graphics.Bitmap
import android.graphics.Matrix

fun Bitmap.mirrored(): Bitmap {
    val matrix =  Matrix();
    matrix.preScale(-1.0f, 1.0f);
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, false)
}