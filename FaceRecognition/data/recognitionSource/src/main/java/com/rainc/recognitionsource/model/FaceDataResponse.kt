package com.rainc.recognitionsource.model

/**
 * Class represents all available outputs of [FaceRecognition.getFaceData]
 */
sealed class FaceDataResponse {
    class Success(val recognitionData: FloatArray) : FaceDataResponse()
    class FaceValidationError(val error: String) : FaceDataResponse()
}