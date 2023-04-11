package com.rainc.facerecognitionmodule.functions

import com.google.mlkit.vision.face.Face

/**
 * Class represents all available outputs of [FaceRecognition.findRecognizableFace]
 */
sealed class RecognitionResponse {
    class Success(val recognizableFace: Recognizable, val face: Face) : RecognitionResponse()
    class UserNotFound(val face: Face) : RecognitionResponse()
    object FaceNotFound : RecognitionResponse()
}