package com.psu.accessapplication.model

import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceLandmark
import com.psu.accessapplication.extentions.asyncJob
import com.psu.accessapplication.extentions.distance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FaceModelFactory @Inject constructor() {

    suspend fun createFaceModel(
        commonFaceParam: List<FaceLandmark>,
        faceContours: List<FaceContour>
    ): FaceModel = withContext(Dispatchers.Default) {
        val findEyesDistanceTask = asyncJob { commonFaceParam.getDistanceBetweenEyes() }
        val findLEyeAndMouseDistance = asyncJob { commonFaceParam.getDistanceBetweenLEyeMouth() }
        val findREyeAndMouseDistance = asyncJob { commonFaceParam.getDistanceBetweenREyeMouth() }
        val findNoseAndMouseDistanceTask = asyncJob { commonFaceParam.getDistanceBetweenMouthAndNose() }
        val findLEyeAndNoseDistanceTask = asyncJob { commonFaceParam.getDistanceBetweenLEyeNose() }
        val findREyeAndNoseDistanceTask = asyncJob { commonFaceParam.getDistanceBetweenREyeNose() }
        val findMouseWeightTask = asyncJob { commonFaceParam.getMouthWidth() }
        val findFaceWidthTask = asyncJob { faceContours.getFaceWidth() }
        val findFaceHeightTask = asyncJob { faceContours.getFaceHeight() }
        return@withContext FaceModel(
            eyesDistance = findEyesDistanceTask.await(),
            lEyeAndMouthDistance = findLEyeAndMouseDistance.await(),
            rEyeAndMouthDistance = findREyeAndMouseDistance.await(),
            mouthWidth = findMouseWeightTask.await(),
            noseAndMouthDistance = findNoseAndMouseDistanceTask.await(),
            lEyeAndNoseDistance = findLEyeAndNoseDistanceTask.await(),
            rEyeAndNoseDistance = findREyeAndNoseDistanceTask.await(),
            faceWidth = findFaceWidthTask.await(),
            faceHeight = findFaceHeightTask.await()
        )
    }

    fun List<FaceLandmark>.getDistanceBetweenEyes(): Double? {
        return getDistance(FaceLandmark.LEFT_EAR, FaceLandmark.RIGHT_EYE)
    }

    fun List<FaceLandmark>.getDistanceBetweenLEyeMouth(): Double? {
        return getDistance(FaceLandmark.LEFT_EAR, FaceLandmark.MOUTH_BOTTOM)
    }

    fun List<FaceLandmark>.getDistanceBetweenREyeMouth(): Double? {
        return getDistance(FaceLandmark.RIGHT_EYE, FaceLandmark.MOUTH_BOTTOM)
    }

    fun List<FaceLandmark>.getMouthWidth(): Double? {
        return getDistance(FaceLandmark.MOUTH_LEFT, FaceLandmark.MOUTH_RIGHT)
    }

    fun List<FaceLandmark>.getDistanceBetweenMouthAndNose(): Double? {
        return getDistance(FaceLandmark.NOSE_BASE, FaceLandmark.MOUTH_BOTTOM)
    }

    fun List<FaceLandmark>.getDistanceBetweenLEyeNose(): Double? {
        return getDistance(FaceLandmark.LEFT_EAR, FaceLandmark.NOSE_BASE)
    }

    fun List<FaceLandmark>.getDistanceBetweenREyeNose(): Double? {
        return getDistance(FaceLandmark.RIGHT_EYE, FaceLandmark.NOSE_BASE)
    }

    private fun List<FaceLandmark>.getDistance(firstObject: Int, secondObject: Int): Double? {
        val firstFaceObject = find { it.landmarkType == firstObject }
        val secondFaceObject = find { it.landmarkType == secondObject }
        return if (firstFaceObject != null && secondFaceObject != null) {
            firstFaceObject.position.distance(secondFaceObject.position)
        } else null
    }

    private fun List<FaceContour>.getFaceWidth(): Double? {
        val param = find { it.faceContourType == FaceContour.FACE } ?: return null
        val sortedParam = param.points.sortedBy { it.x }
        return sortedParam[sortedParam.lastIndex].x.toDouble() - sortedParam[0].x.toDouble()
    }

    private fun List<FaceContour>.getFaceHeight(): Double? {
        val param = find { it.faceContourType == FaceContour.FACE } ?: return null
        val sortedParam = param.points.sortedBy { it.y }
        return sortedParam[sortedParam.lastIndex].y.toDouble() - sortedParam[0].y.toDouble()
    }
}
