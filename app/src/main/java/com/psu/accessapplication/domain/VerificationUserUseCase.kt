package com.psu.accessapplication.domain

import android.graphics.Bitmap
import com.psu.accessapplication.model.AnalyzeResult
import com.psu.accessapplication.model.Person
import com.psu.accessapplication.tools.PersonVerification
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@ViewModelScoped
class VerificationUserUseCase @Inject constructor (val personVerification: PersonVerification) {

    suspend fun verifyUser(personImage: Bitmap): Deferred<AnalyzeResult> {
        return personVerification.checkPerson(personImage)
    }

    suspend fun analyzeImage(personImage: Bitmap) {
        personVerification.analyzeImageAsync(personImage).await()
    }
}
