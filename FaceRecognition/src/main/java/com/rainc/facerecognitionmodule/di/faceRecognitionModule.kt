package com.rainc.facerecognitionmodule.di

import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.mlkit.vision.face.FaceDetection
import com.rainc.facerecognitionmodule.tools.FaceDetectorOptionsType
import com.rainc.facerecognitionmodule.tools.MobileFaceNet
import com.rainc.facerecognitionmodule.tools.YuvToRgbConverter
import com.rainc.recognitionsource.di.recognitionSourceModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val faceRecognitionModule = module {
    includes(recognitionSourceModel)
    single { Gson() }
    single { Glide.with(androidContext()) }
    single { YuvToRgbConverter(androidContext()) }
    single { MobileFaceNet(androidContext()) }
    single { (type: FaceDetectorOptionsType)  -> FaceDetection.getClient(get { parametersOf(type) }) }
}