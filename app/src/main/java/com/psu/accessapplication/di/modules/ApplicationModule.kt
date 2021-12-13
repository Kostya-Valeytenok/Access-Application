package com.psu.accessapplication.di.modules

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun getDetectorOptions(): FaceDetectorOptions {
        return FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .build()
    }

    @Provides
    @Singleton
    fun getDetector(options: FaceDetectorOptions): FaceDetector {
        return FaceDetection.getClient(options)
    }

    @Provides
    @Singleton
    fun getGlide(@ApplicationContext context: Context): RequestManager {
        return Glide.with(context)
    }
}
