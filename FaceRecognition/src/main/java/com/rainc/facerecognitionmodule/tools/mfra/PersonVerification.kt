package com.rainc.facerecognitionmodule.tools.mfra

import android.graphics.Bitmap
import com.rainc.coroutinecore.extension.asyncJob
import com.rainc.facerecognitionmodule.tools.mfra.data.PersonDataCache
import com.rainc.facerecognitionmodule.tools.mfra.extention.nullableToResult
import com.rainc.facerecognitionmodule.tools.mfra.model.AnalyzeError
import com.rainc.facerecognitionmodule.tools.mfra.model.AnalyzeResult
import com.rainc.facerecognitionmodule.tools.mfra.model.FaceModel
import com.rainc.facerecognitionmodule.tools.mfra.model.Failure
import com.rainc.facerecognitionmodule.tools.mfra.model.Person
import com.rainc.facerecognitionmodule.tools.mfra.model.VerificationCore
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.flow.firstOrNull

internal class PersonVerification(val core: VerificationCore) {

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

    internal open fun getPersonData(): MutableList<Person> {
        return PersonDataCache.cache.value.toMutableList()
    }

    final fun init(persons: List<Person>) {
        PersonDataCache.init(persons)
    }

    final fun destroy() {
        PersonDataCache.clean()
    }
}
