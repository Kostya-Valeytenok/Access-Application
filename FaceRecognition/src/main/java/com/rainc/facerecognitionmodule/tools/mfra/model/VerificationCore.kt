package com.rainc.facerecognitionmodule.tools.mfra.model

import android.graphics.Bitmap
import android.graphics.PointF
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetector
import com.rainc.coroutinecore.tools.CoroutineWorker
import com.rainc.facerecognitionmodule.tools.mfra.extention.compare
import com.rainc.facerecognitionmodule.tools.mfra.extention.nullableToResult
import com.rainc.recognitionsource.tools.ImageTransformManager
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow

internal class VerificationCore (
    private val detector: FaceDetector,
    private val transformManager: ImageTransformManager,
    private val faceModelFactory: FaceModelFactory
) {

    val Algorithm_Accuracy_Persentre = 95.7
    val Algorithm_Absolytly_Accuracy_Persentre = 99.3

    fun init() {
    }

    suspend fun getPersonImage(personPhoto: Bitmap): Deferred<Result<Bitmap>> {
        val result: CompletableDeferred<Result<Bitmap>> = CompletableDeferred()
        val image = InputImage.fromBitmap(personPhoto, 0)
        val points = mutableListOf<PointF>()
        detector.process(image).addOnSuccessListener { faces ->
            println("step 1 bas image was analyzed")

            runCatching {
                faces.first().allContours.forEach {
                    points.addAll(it.points)
                }
            }.onSuccess {
                CoroutineWorker.launch {
                    runCatching { personPhoto.transformImageToFaceImage(points) }
                        .onSuccess { result.complete(it) }
                        .onFailure { result.complete(Result.failure(it)) }
                }
            }.onFailure {
                result.complete(Result.failure(it))
            }
        }.addOnFailureListener {
            result.complete(Result.failure(it))
        }
        return result
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

    suspend fun findPerson(personPhoto: Bitmap, persons: List<Person>): MutableSharedFlow<AnalyzeResult> {
        val result = MutableSharedFlow<AnalyzeResult>()
        val image = InputImage.fromBitmap(personPhoto, 0)
        detector.process(image).addOnSuccessListener { faces ->
            println("analyze Image \n")

            println("Contours \n")

            faces.first().allContours.forEach {
                println("type ${it.faceContourType} \n\n ${it.points}")
            }

            CoroutineWorker.launch {
                val person = faces.first()
                    .analyze()
                    .findThisPerson(persons)
                println("end")
                result.emit(person)
            }
        }.addOnFailureListener {
            println("Transformed image Analyze Error")
            CoroutineWorker.launch { result.emit(Failure("Transformed image Analyze Error")) }
        }

        return result
    }

    private suspend fun Face.analyze(): FaceModel {
        val face = faceModelFactory.createFaceModel(
            commonFaceParam = allLandmarks,
            faceContours = allContours
        )
        println(face.modelData)
        return face
    }

    private suspend fun FaceModel.findThisPerson(persons: List<Person>): AnalyzeResult {
        val job = Job()
        job.start()
        var person: Successful? = null
        val jobScope = CoroutineWorker.getChild()
        val result = mutableListOf<Pair<Double, Person>>()
        val taskList = compareWithPersons(scope = jobScope, persons = persons)
        result.addAll(taskList.waitForResult(jobScope = jobScope, findAction = { person = it }, job))
        job.join()
        return person ?: result.findPersonWithBestSimilarity()
    }

    private fun FaceModel.compareWithPersons(scope: CoroutineScope, persons: List<Person>): MutableList<Deferred<Pair<Double, Person>>> {
        val taskList = mutableListOf<Deferred<Pair<Double, Person>>>()
        persons.forEach { person ->
            taskList.add(
                scope.async { Pair(compare(person.face), person) }
            )
        }
        return taskList
    }

    private suspend fun MutableList<Deferred<Pair<Double, Person>>>.waitForResult(
        jobScope: CoroutineScope,
        findAction: (Successful) -> Unit,
        job: CompletableJob
    ): MutableList<Pair<Double, Person>> {
        val result = mutableListOf<Pair<Double, Person>>()
        forEach {
            val compareResult = it.await()
            result.add(compareResult)
            if (compareResult.first >= Algorithm_Absolytly_Accuracy_Persentre) {
                println("FIND PERSON")
                job.complete()
                findAction.invoke(Successful(similarity = compareResult.first, person = compareResult.second))
                jobScope.cancel()
                return@forEach
            }
        }
        job.complete()
        return result
    }

    private fun List<Pair<Double, Person>>.findPersonWithBestSimilarity(): AnalyzeResult {
        val sortedList = sortedBy { it.first }
        sortedList.forEach {
            println("Similarity: ${it.first} person: ${it.second.id}")
        }
        val result = filter { it.first >= Algorithm_Accuracy_Persentre }.maxByOrNull { it.first }
        return if(result == null)  Failure("Not Find")
        else Successful(similarity = result.first, person = result.second)
    }

    private suspend fun Bitmap.transformImageToFaceImage(points: MutableList<PointF>): Result<Bitmap> {
        return transformManager.transformImageToFaceImage(personPhoto = this, points)
    }
}
