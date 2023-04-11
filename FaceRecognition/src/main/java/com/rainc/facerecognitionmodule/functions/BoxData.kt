package com.rainc.facerecognitionmodule.functions

import com.google.mlkit.vision.face.Face

/**
 * Class that used by [FaceRecognitionGraphic] to draw some overlay
 */
sealed class BoxData(val face: Face) {
    class NewFaceData(face: Face, val isValid: Boolean) : BoxData(face)
    class RecognitionData(face: Face, val user: Recognizable?) : BoxData(face)
}