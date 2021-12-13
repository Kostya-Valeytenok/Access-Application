package com.psu.accessapplication.domain

import android.graphics.Bitmap
import com.psu.accessapplication.extentions.collectOnce
import com.psu.accessapplication.model.Person
import com.psu.accessapplication.tools.PersonVerification
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

@ViewModelScoped
class VerificationUserUseCase @Inject constructor (val personVerification: PersonVerification) {

    suspend fun verifyUser(personImage: Bitmap): MutableSharedFlow<Result<Person>> {
        return personVerification.checkPerson(personImage)
    }

    suspend fun analyzeImage(personImage: Bitmap) {
        personVerification.analyzeImage(personImage).collectOnce { }
    }
}
