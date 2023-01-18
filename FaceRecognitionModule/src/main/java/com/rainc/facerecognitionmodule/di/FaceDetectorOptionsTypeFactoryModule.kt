package com.rainc.facerecognitionmodule.di

import com.google.mlkit.vision.face.FaceDetectorOptions
import com.rainc.facerecognitionmodule.tools.FaceDetectorOptionsType
import org.koin.dsl.module

val faceDetectorOptionsTypeModule = module {
    factory{ (type: FaceDetectorOptionsType)  ->
        when(type){
            FaceDetectorOptionsType.Full ->
                FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                    .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
                    .build()
            FaceDetectorOptionsType.Simple ->
                FaceDetectorOptions.Builder()
                    .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                    .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
                    .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                    .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                    .setMinFaceSize(1F)
                    .build()
        }
    }
}