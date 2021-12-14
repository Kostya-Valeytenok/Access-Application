package com.psu.accessapplication.tools

import android.graphics.Bitmap
import com.psu.accessapplication.extentions.asyncJob
import com.psu.accessapplication.extentions.collectOnce
import com.psu.accessapplication.model.FaceModel
import com.psu.accessapplication.model.Person
import com.psu.accessapplication.model.VerificationCore
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonVerification @Inject constructor(val core: VerificationCore) {

    suspend fun checkPerson(personImage: Bitmap): MutableSharedFlow<Result<Person>> =
        withContext(Dispatchers.Default) {
            val person = MutableSharedFlow<Result<Person>>()

            core.getPersonImage(personImage).first {
                println("step 1: End ")
                it.onSuccess {
                    println("step 2: Transformed image has been got")
                    findPerson(it, person)
                    println("pre-return")
                }.onFailure {
                    println("AnalyzeError")
                    person.emit(Result.failure(AnalyzeError()))
                }
                true
            }
            println("return")
            return@withContext person
        }

    suspend fun analyzeImageAsync(personImage: Bitmap): Deferred<Result<FaceModel>> = asyncJob {
        val transformedImage = core.getPersonImage(personImage).firstOrNull()
        transformedImage?.let {
            it.onSuccess {
                return@asyncJob (core.analyzeImage(it).firstOrNull() ?: Result.failure(AnalyzeError()))
            }
            it.onFailure { return@asyncJob (Result.failure(AnalyzeError())) }
        }
        return@asyncJob (Result.failure(AnalyzeError()))
    }

    private suspend fun findPerson(
        personImage: Bitmap,
        resultProvider: MutableSharedFlow<Result<Person>>
    ) {
        core.findPerson(personImage, getPersonData()).collectOnce {
            resultProvider.emit(it)
        }
    }

    protected open fun getPersonData(): MutableList<Person> {
        return PersonDataCache.cache
    }

    final fun init(persons: List<Person>) {
        PersonDataCache.init(persons)
    }

    final fun destroy() {
        PersonDataCache.clean()
    }
}
