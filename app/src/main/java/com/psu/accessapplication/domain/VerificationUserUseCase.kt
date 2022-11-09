package com.psu.accessapplication.domain

import android.graphics.Bitmap
import com.psu.accessapplication.model.AnalyzeResult
import com.psu.accessapplication.tools.PersonVerification
import kotlinx.coroutines.Deferred


class VerificationUserUseCase(private val personVerification: PersonVerification) {

    suspend fun verifyUser(personImage: Bitmap): Deferred<AnalyzeResult> {
        return personVerification.checkPerson(personImage)
    }

    suspend fun analyzeImage(personImage: Bitmap) {
        personVerification.analyzeImageAsync(personImage).await()
    }
}
