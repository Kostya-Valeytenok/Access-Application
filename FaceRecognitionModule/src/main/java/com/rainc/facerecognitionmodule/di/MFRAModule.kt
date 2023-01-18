package com.rainc.facerecognitionmodule.di

import com.rainc.facerecognitionmodule.tools.FaceDetectorOptionsType
import com.rainc.facerecognitionmodule.tools.mfra.PersonVerification
import com.rainc.facerecognitionmodule.tools.mfra.domain.VerificationUserUseCase
import com.rainc.facerecognitionmodule.tools.mfra.model.VerificationCore
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val mfraModule = module {
    single { VerificationCore(
        detector = get{ parametersOf(FaceDetectorOptionsType.Full) },
        transformManager = get(),
        faceModelFactory = get()
    ) }
    single { PersonVerification(core = get()) }
    single { VerificationUserUseCase(personVerification = get()) }
}