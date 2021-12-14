package com.psu.accessapplication.model

import android.graphics.Bitmap
import android.graphics.PointF
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetector
import com.psu.accessapplication.extentions.compare
import com.psu.accessapplication.extentions.nullableToResult
import com.psu.accessapplication.tools.AnalyzeError
import com.psu.accessapplication.tools.CoroutineWorker
import com.psu.accessapplication.tools.ImageTransformManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject

class VerificationCore @Inject constructor(
    private val detector: FaceDetector,
    private val transformManager: ImageTransformManager,
    private val faceModelFactory: FaceModelFactory
) {

    val Algorithm_Accuracy_Persentre = 95.7
    val Algorithm_Absolytly_Accuracy_Persentre = 99.3

    fun init() {
    }

    suspend fun getPersonImage(personPhoto: Bitmap): MutableSharedFlow<Result<Bitmap>> {
        val bitmapFlow = MutableSharedFlow<Result<Bitmap>>()
        val image = InputImage.fromBitmap(personPhoto, 0)
        val points = mutableListOf<PointF>()
        detector.process(image).addOnSuccessListener { faces ->

            println("step 1 bas image was analyzed")

            faces.first().allContours.forEach {
                points.addAll(it.points)
            }

            CoroutineWorker.launch {
                println("send Transform image")
                bitmapFlow.emit(personPhoto.transformImageToFaceImage(points))
            }
        }.addOnFailureListener {
            CoroutineWorker.launch { bitmapFlow.emit(Result.failure(it)) }
        }
        return bitmapFlow
    }

    suspend fun analyzeImage(personPhoto: Bitmap): MutableSharedFlow<Result<FaceModel>> {
        val result = MutableSharedFlow<Result<FaceModel>>()
        val image = InputImage.fromBitmap(personPhoto, 0)
        detector.process(image).addOnSuccessListener { faces ->
            CoroutineWorker.launch {
                val person = faces.first().analyze()
                result.emit(person.nullableToResult())
            }
        }
        return result
    }

    suspend fun findPerson(personPhoto: Bitmap, persons: List<Person>): MutableSharedFlow<Result<Person>> {
        val result = MutableSharedFlow<Result<Person>>()
        val image = InputImage.fromBitmap(personPhoto, 0)
        detector.process(image).addOnSuccessListener { faces ->
            println("analyze Image \n")

            CoroutineWorker.launch {
                val person = faces.first()
                    .analyze()
                    .findThisPerson(persons)
                println("end")
                result.emit(person.nullableToResult())
            }
        }.addOnFailureListener {
            println("Transformed image Analyze Error")
            CoroutineWorker.launch { result.emit(Result.failure(AnalyzeError())) }
        }

        return result
    }

    private suspend fun Face.analyze(): FaceModel {
        val face = faceModelFactory.createFaceModel(allLandmarks)
        println(face.modelData)
        return face
    }

    private suspend fun FaceModel.findThisPerson(persons: List<Person>): Person? {
        val job = Job()
        job.start()
        var person: Person? = null
        val jobScope = CoroutineWorker.getChild()
        val result = mutableListOf<Pair<Double, Person>>()
        val taskList = compareWithPersons(scope = jobScope, persons = persons)
        result.addAll(taskList.waitForResult(jobScope = jobScope, findAction = { person = it }, job))
        job.join()
        return person ?: result.findPersonWithBestSimilarity()
    }

    private fun FaceModel.compareWithPersons(scope: CoroutineScope, persons: List<Person>): MutableList<Deferred<Pair<Double, Person>>> {
        val taskList = mutableListOf<Deferred<Pair<Double, Person>>>()
        persons.forEach { persons ->
            taskList.add(
                scope.async { Pair(compare(persons.face), persons) }
            )
        }
        return taskList
    }

    private suspend fun MutableList<Deferred<Pair<Double, Person>>>.waitForResult(
        jobScope: CoroutineScope,
        findAction: (Person) -> Unit,
        job: CompletableJob
    ): MutableList<Pair<Double, Person>> {
        val result = mutableListOf<Pair<Double, Person>>()
        forEach {
            val compareResult = it.await()
            result.add(compareResult)
            if (compareResult.first >= Algorithm_Absolytly_Accuracy_Persentre) {
                println("FIND PERSON")
                job.complete()
                findAction.invoke(compareResult.second)
                jobScope.cancel()
                return@forEach
            }
        }
        job.complete()
        return result
    }

    private fun List<Pair<Double, Person>>.findPersonWithBestSimilarity(): Person? {
        val sortedList = sortedBy { it.first }
        sortedList.forEach {
            println("Similarity: ${it.first} person: ${it.second.id}")
        }
        return filter { it.first >= Algorithm_Accuracy_Persentre }.maxByOrNull { it.first }?.second
    }

    private suspend fun Bitmap.transformImageToFaceImage(points: MutableList<PointF>): Result<Bitmap> {
        return transformManager.transformImageToFaceImage(personPhoto = this, points)
    }
}
