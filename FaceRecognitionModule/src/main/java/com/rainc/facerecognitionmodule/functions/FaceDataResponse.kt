package com.rainc.facerecognitionmodule.functions

/**
 * Class represents all available outputs of [FaceRecognition.getFaceData]
 */
sealed class FaceDataResponse {
    class Success(val recognitionData: FloatArray) : FaceDataResponse()
    class FaceValidationError(val error: String) : FaceDataResponse()
}