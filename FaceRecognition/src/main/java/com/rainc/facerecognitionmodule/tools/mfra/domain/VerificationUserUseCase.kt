package com.rainc.facerecognitionmodule.tools.mfra.domain

import android.graphics.Bitmap
import com.rainc.facerecognitionmodule.tools.mfra.model.AnalyzeResult
import com.rainc.facerecognitionmodule.tools.mfra.PersonVerification
import kotlinx.coroutines.Deferred


class VerificationUserUseCase internal constructor (internal val personVerification: PersonVerification) {

    suspend fun verifyUser(personImage: Bitmap): Deferred<AnalyzeResult> {
        return personVerification.checkPerson(personImage)
    }

    suspend fun analyzeImage(personImage: Bitmap) {
        personVerification.analyzeImageAsync(personImage).await()
    }
}
