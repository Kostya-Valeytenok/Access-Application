package com.rainc.facerecognitionmodule.functions

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.Image
import android.util.Log
import android.util.Pair
import androidx.annotation.WorkerThread
import androidx.core.graphics.scale
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceDetector
import com.rainc.facerecognitionmodule.R
import com.rainc.recognitionsource.model.FaceDataResponse
import com.rainc.facerecognitionmodule.tools.FaceDetectorOptionsType
import com.rainc.facerecognitionmodule.tools.MobileFaceNet
import com.rainc.facerecognitionmodule.tools.YuvToRgbConverter
import com.rainc.recognitionsource.model.Recognizable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Face recognition that uses model from MobileFaceNet
 * @see <a href="https://arxiv.org/ftp/arxiv/papers/1804/1804.07573.pdf">MobileFaceNet research</a>
 * @see <a href="https://medium.com/@estebanuri/real-time-face-recognition-with-android-tensorflow-lite-14e9c6cc53a5">Medium</a>
 * @see <a href="https://github.com/sirius-ai/MobileFaceNet_TF">Tensor flow model that was converted to tflite model</a>
 */
object FaceRecognition : KoinComponent {
    /**
     * Face recognition model
     */
    private val model: MobileFaceNet by inject()

    /**
     * Converter for Yuv images to RGB
     */
    private val yuvToRgbConverter: YuvToRgbConverter by inject()


    //Face detector from MLKit
    private val faceDetector: FaceDetector by inject{ parametersOf(FaceDetectorOptionsType.Simple) }

    /**
     * @see MAX_DISTANCE
     */
    const val DEFAULT_MAX_DISTANCE = 0.8F

    /**
     * Please set emptyList when you are done (to clear memory)
     */
    var RECOGNIZABLE_FACES: List<Recognizable> = emptyList()

    /**
     * Max distance helps to drop faces which have big distance from input image.
     * Example: You take photo of your face, if your face in database your distance will be lower then 1 (Two identical photos has 0 distance.), but for other faces it will be more then 1.
     * But sometimes other faces have les then 1 and [MAX_DISTANCE] helps to drop these faces
     */
    var MAX_DISTANCE = DEFAULT_MAX_DISTANCE

    private const val TAG = "FaceRecognition"

    //Image size for ml model
    private const val TF_MODEL_INPUT_SIZE = 112

    //Normalization parameter
    private const val IMAGE_MEAN = 127.5F

    //Normalization parameter
    private const val IMAGE_STD = 128f

    private lateinit var context: Application

    fun init(context: Application) {
        FaceRecognition.context = context
    }


    //Buffer for load image data
    private val imageBuffer by lazy {
        ByteBuffer.allocate(1 * TF_MODEL_INPUT_SIZE * TF_MODEL_INPUT_SIZE * 3 * 4).order(ByteOrder.nativeOrder())
    }

    //Tensor buffer that will be processed
    private val tensorBuffer by lazy {
        TensorBuffer.createFixedSize(
            intArrayOf(1, TF_MODEL_INPUT_SIZE, TF_MODEL_INPUT_SIZE, 3),
            DataType.FLOAT32
        )
    }

    //Image pixels buffer
    private val intValues by lazy { IntArray(TF_MODEL_INPUT_SIZE * TF_MODEL_INPUT_SIZE) }

    /**
     * Try to get face data from rawImage
     * */
    @WorkerThread
    fun getFaceData(rawImage: RecognitionImage): FaceDataResponse {
        try {
            val face = getFaceFromImage(rawImage)

            if (face != null) {
                val errorMessage = validateFace(face)

                if (errorMessage != null) return FaceDataResponse.FaceValidationError(errorMessage)

                val imageForProcessing = rawImage.prepareForRecognition(face)
                val recognitionData = process(imageForProcessing)
                return FaceDataResponse.Success(recognitionData)
            } else{
                println("face is null")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error when try to get recognizable", e)
            println(e)
        }

        return FaceDataResponse.FaceValidationError("Face not found")
    }

    /**
     * Find user in [RECOGNIZABLE_FACES] collection
     *
     * @param rawImage with face to find [Recognizable]
     */
    @WorkerThread
    fun findRecognizableFace(rawImage: RecognitionImage): RecognitionResponse {
        val startExecutionTime = System.currentTimeMillis()

        return try {
            val face = getFaceFromImage(rawImage)

            if (face != null) {
                val imageForProcessing = rawImage.prepareForRecognition(face)
                val user = findRecognizableFace(process(imageForProcessing))

                if (user != null) {
                    RecognitionResponse.Success(user, face)
                } else {
                    RecognitionResponse.UserNotFound(face)
                }
            } else {
                RecognitionResponse.FaceNotFound
            }
        } catch (e: Exception) {
            RecognitionResponse.FaceNotFound
        } finally {
            Log.d(TAG, "Find user execution time: ${System.currentTimeMillis() - startExecutionTime} ms")
        }
    }

    @WorkerThread
    fun getFaceAsBitmap(rawImage: RecognitionImage): Bitmap? {
        val face = getFaceFromImage(rawImage)

        return try {
            if (face != null) {
                return rawImage.prepareForRecognition(face)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Find [Face] in image
     *
     * @return [Face] if was found or 'null'
     */
    fun getFaceFromImage(image: RecognitionImage): Face? {
        val rotation = image.rotation
        val faceDetectorImage = when (image) {
            is RecognitionImage.BitmapRecognitionImage -> InputImage.fromBitmap(image.image, rotation)
            is RecognitionImage.MediaRecognitionImage -> InputImage.fromMediaImage(image.image, rotation)
        }

        val faces = Tasks.await(faceDetector.process(faceDetectorImage))
        return faces.firstOrNull()
    }

    fun validateFace(face: Face): String? {
        Log.d(TAG, "ValidateImage face's data: $face")

        return if (face.smilingProbability ?: 0.0F > 0.5F) {
            context.getString( R.string.validation_dont_smile)
        } else if (face.headEulerAngleX.absoluteValue > 10) {
            context.getString(R.string.validation_keep_phone_in_front)
        } else if (face.headEulerAngleY.absoluteValue > 10 || face.headEulerAngleZ.absoluteValue > 10) {
            context.getString(R.string.validation_keep_head_straight)
        } else {
            null
        }
    }

    /**
     * Process the [imageForProcessing] through the [model]
     * @return [FloatArray] with 192 output size
     */
    @Synchronized
    private fun process(imageForProcessing: Bitmap): FloatArray {
        // Pre-process the image data from 0-255 int to normalized float based model
        imageForProcessing.getPixels(intValues, 0, imageForProcessing.width, 0, 0, imageForProcessing.width, imageForProcessing.height)

        imageBuffer.rewind()

        for (i in 0 until TF_MODEL_INPUT_SIZE) {
            for (j in 0 until TF_MODEL_INPUT_SIZE) {
                val pixelValue: Int = intValues[i * TF_MODEL_INPUT_SIZE + j]
                imageBuffer.putFloat(((pixelValue shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                imageBuffer.putFloat(((pixelValue shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                imageBuffer.putFloat(((pixelValue and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
            }
        }

        tensorBuffer.loadBuffer(imageBuffer)

        return model.process(tensorBuffer).outputFeature0AsTensorBuffer.floatArray
    }

    /**
     * Find user from [RECOGNIZABLE_FACES] collection by [inputFaceData] face data
     * @param [inputFaceData] face data from [MobileFaceNet] model
     * @return [Recognizable] if it was found or 'null'
     */
    private fun findRecognizableFace(inputFaceData: FloatArray): Recognizable? {
        val usersWithFacesData: MutableMap<Recognizable, Float> = mutableMapOf()

        //Iterate through recognizableFaces and calculate every face distance to inputFaceData
        for (recognizable in RECOGNIZABLE_FACES) {
            usersWithFacesData[recognizable] = l2Norm(inputFaceData, recognizable.data)
        }

        var bestRecognizableFaceWithFaceDistance: Pair<Recognizable, Float>? = null

        //Iterate through usersWithFacesData and find the user with the best distance
        for (userWithFace in usersWithFacesData) {
            val userFaceDistance: Float = userWithFace.value

            if (userFaceDistance > MAX_DISTANCE) {
                continue
            }

            if (bestRecognizableFaceWithFaceDistance == null || userFaceDistance < bestRecognizableFaceWithFaceDistance.second) {
                bestRecognizableFaceWithFaceDistance = Pair(userWithFace.key, userFaceDistance)
            }
        }

        return if (bestRecognizableFaceWithFaceDistance != null) {
            Log.d(TAG, bestRecognizableFaceWithFaceDistance.toString())

            bestRecognizableFaceWithFaceDistance.first
        } else {
            null
        }
    }

    /**
     * Calculate distance between two float array (less is better)
     * @return 0 for the same [x1] and [x2] and >1 for big distance
     */
    private fun l2Norm(x1: FloatArray, x2: FloatArray): Float {
        var sum = 0.0f
        for (i in x1.indices) {
            sum += (x1[i] - x2[i]).pow(2)
        }
        return sqrt(sum)
    }

    private fun RecognitionImage.prepareForRecognition(face: Face): Bitmap {
        return when (this) {
            is RecognitionImage.BitmapRecognitionImage -> image.prepareForRecognition(rotation, face)
            is RecognitionImage.MediaRecognitionImage -> image.prepareForRecognition(rotation, face)
        }
    }

    private fun Image.prepareForRecognition(
        rotation: Int,
        face: Face,
    ): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(rotation.toFloat())

        val bitmapFromCameraImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        yuvToRgbConverter.yuvToRgb(this, bitmapFromCameraImage)

        val faceBox = face.boundingBox
        val source = Bitmap.createBitmap(bitmapFromCameraImage, 0, 0, this.width, this.height, matrix, false)

        var width = faceBox.width()
        var height = faceBox.height()

        if ((faceBox.left + width) > source.width) {
            width = source.width - faceBox.left
        }
        if ((faceBox.top + height) > source.height) {
            height = source.height - faceBox.top
        }

        return Bitmap.createBitmap(
            source, faceBox.left,
            faceBox.top,
            width,
            height
        ).scale(TF_MODEL_INPUT_SIZE, TF_MODEL_INPUT_SIZE)
    }

    private fun Bitmap.prepareForRecognition(rotation: Int, face: Face): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(rotation.toFloat())

        val rect = face.boundingBox
        val source = Bitmap.createBitmap(this, 0, 0, this.width, this.height, matrix, false)

        var width = rect.width()
        var height = rect.height()

        if ((rect.left + width) > source.width) {
            width = source.width - rect.left
        }
        if ((rect.top + height) > source.height) {
            height = source.height - rect.top
        }

        return Bitmap.createBitmap(
            source, rect.left,
            rect.top,
            width,
            height
        ).scale(TF_MODEL_INPUT_SIZE, TF_MODEL_INPUT_SIZE)
    }
}