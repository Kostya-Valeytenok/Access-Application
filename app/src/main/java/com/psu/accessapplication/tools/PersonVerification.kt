package com.psu.accessapplication.tools

import android.graphics.Bitmap
import com.psu.accessapplication.extentions.collectOnce
import com.psu.accessapplication.model.FaceModel
import com.psu.accessapplication.model.Person
import com.psu.accessapplication.model.VerificationCore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PersonVerification @Inject constructor(val core: VerificationCore) {

    suspend fun checkPerson(personImage: Bitmap): MutableSharedFlow<Result<Person>> =
        withContext(Dispatchers.Default) {
            val person = MutableSharedFlow<Result<Person>>()

            core.getPersonImage(personImage).collectOnce {
                it.onSuccess {
                    findPerson(it, person)
                    println("pre-return")
                }
                it.onFailure {
                    println("AnalyzeError")
                    person.emit(Result.failure(AnalyzeError()))
                }
            }
            println("return")
            return@withContext person
        }

    suspend fun analyzeImage(personImage: Bitmap): MutableSharedFlow<Result<FaceModel>> {
        val person = MutableSharedFlow<Result<FaceModel>>()
        core.getPersonImage(personImage).collectOnce {
            it.onSuccess {
                core.analyzeImage(it).collectOnce {
                    person.emit(it)
                }
            }
            it.onFailure {
                person.emit(Result.failure(AnalyzeError()))
            }
        }
        return person
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
