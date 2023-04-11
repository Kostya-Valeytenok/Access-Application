package com.rainc.facerecognitionmodule.functions

import android.graphics.Bitmap
import android.media.Image

/**
 * Class represents different type of input image for [FaceRecognition]
 */
sealed class RecognitionImage(val rotation: Int) {
    class BitmapRecognitionImage(rotation: Int, val image: Bitmap) : RecognitionImage(rotation)
    class MediaRecognitionImage(rotation: Int, val image: Image) : RecognitionImage(rotation)
}
