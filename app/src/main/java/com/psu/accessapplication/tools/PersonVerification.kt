package com.psu.accessapplication.tools

import android.graphics.Bitmap
import com.psu.accessapplication.extentions.asyncJob
import com.psu.accessapplication.extentions.nullableToResult
import com.psu.accessapplication.model.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

class PersonVerification @Inject constructor(val core: VerificationCore) {

    suspend fun checkPerson(personImage: Bitmap): Deferred<AnalyzeResult> = asyncJob {
        var result: AnalyzeResult = Failure("Not Init")
        val personFaceImage = core.getPersonImage(personImage).await()
        personFaceImage
            .onSuccess {
                findPerson(it)
                    .nullableToResult()
                    .onSuccess { result = it }
                    .onFailure { result = Failure("Empty RESULT") }
            }
            .onFailure { result = Failure(it.message?:"get person image step error") }
        return@asyncJob result
    }

    suspend fun analyzeImageAsync(personImage: Bitmap): Deferred<Result<FaceModel>> = asyncJob {
        val transformedImage = core.getPersonImage(personImage).await()
        transformedImage.let {
            it.onSuccess {
                return@asyncJob (core.analyzeImage(it).firstOrNull() ?: Result.failure(AnalyzeError()))
            }
            it.onFailure { return@asyncJob (Result.failure(AnalyzeError())) }
        }
        return@asyncJob (Result.failure(AnalyzeError()))
    }

    private suspend fun findPerson(
        personImage: Bitmap
    ): AnalyzeResult? {
        return core.findPerson(personImage, getPersonData()).firstOrNull()
    }

    protected open fun getPersonData(): MutableList<Person> {
        return PersonDataCache.cache.value.toMutableList()
    }

    final fun init(persons: List<Person>) {
        PersonDataCache.init(persons)
    }

    final fun destroy() {
        PersonDataCache.clean()
    }
}
