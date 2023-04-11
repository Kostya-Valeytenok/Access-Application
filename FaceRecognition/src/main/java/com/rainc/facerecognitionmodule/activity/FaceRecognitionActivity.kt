package com.rainc.facerecognitionmodule.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.Image
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.OrientationEventListener
import android.view.Surface
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rainc.facerecognitionmodule.R
import com.rainc.facerecognitionmodule.functions.BoxData
import com.rainc.facerecognitionmodule.functions.FaceDataResponse
import com.rainc.facerecognitionmodule.functions.FaceRecognition
import com.rainc.facerecognitionmodule.functions.RecognitionActivityResult
import com.rainc.facerecognitionmodule.functions.RecognitionImage
import com.rainc.facerecognitionmodule.functions.RecognitionResponse
import com.rainc.facerecognitionmodule.functions.Recognizable
import com.rainc.facerecognitionmodule.dialog.AddRecognizableDialog
import com.rainc.facerecognitionmodule.dialog.RecognizableBottomSheet
import com.rainc.facerecognitionmodule.extentions.gone
import com.rainc.facerecognitionmodule.extentions.isGranted
import com.rainc.facerecognitionmodule.extentions.showToast
import com.rainc.facerecognitionmodule.extentions.updateConstraints
import com.rainc.facerecognitionmodule.extentions.visible
import com.rainc.facerecognitionmodule.tools.YuvToRgbConverter
import com.rainc.facerecognitionmodule.view.FaceRecognitionGraphic
import com.rainc.facerecognitionmodule.view.FoundRecognizableBudgeView
import com.rainc.facerecognitionmodule.view.GraphicOverlay
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.parcelize.Parcelize
import org.koin.android.ext.android.inject
import java.io.File
import java.util.concurrent.Executors

/**
 * Activity that can find among recognizable or create new recognizable
 */
public class FaceRecognitionActivity : AppCompatActivity() {
    private val yuvConverter: YuvToRgbConverter by inject()

    companion object {
        private const val KEY_INPUT_PARAMS = "KEY_INPUT_PARAMS"
        private const val KEY_RESPONSE = "KEY_RESPONSE"

        @JvmOverloads
        @JvmStatic
      public  fun getLaunchIntent(context: Context, inputParams: InputParams, recognizable: List<Recognizable> = emptyList()): Intent {
           // FaceRecognition.RECOGNIZABLE_FACES = recognizable

            if (inputParams is InputParams.Recognition) {
                FaceRecognition.MAX_DISTANCE = inputParams.maxDistance
            }

            return Intent(context, FaceRecognitionActivity::class.java).apply {
                putExtra(KEY_INPUT_PARAMS, inputParams)
            }
        }

        @JvmStatic
        fun resultFromIntent(intent: Intent): RecognitionActivityResult? {
            return try {
                intent.getParcelableExtra(KEY_RESPONSE)
            } catch (e: Exception) {
                null
            }
        }
    }

    @Suppress("CanSealedSubClassBeObject", "PARCELABLE_PRIMARY_CONSTRUCTOR_IS_EMPTY")
    sealed class InputParams : Parcelable {
        @Parcelize
        class CaptureNewImage : InputParams()

        @Parcelize
        class Recognition(val maxDistance: Float = FaceRecognition.DEFAULT_MAX_DISTANCE) : InputParams()
    }

    private val analyzerExecutor = Executors.newSingleThreadExecutor()
    private val analyzer = FaceAnalyzer()

    private val orientationEventListener by lazy {
        OrientationListener()
    }
    private val cameraProvider: ProcessCameraProvider by lazy {
        ProcessCameraProvider.getInstance(this).get()
    }

    private val tempFile: File by lazy {
        File(cacheDir, "TempRecognitionImage.jpg").apply {
            createNewFile()
            deleteOnExit()
        }
    }
    private val recognized by lazy {
        mutableMapOf<Long, Recognizable>()
    }
    private val inputParams: InputParams by lazy {
        intent.getParcelableExtra(KEY_INPUT_PARAMS)!!
    }
    private val naturalRotation by lazy {
        windowManager.defaultDisplay.rotation
    }

    private lateinit var root: ConstraintLayout
    private lateinit var previewView: PreviewView
    private lateinit var takePictureButton: FloatingActionButton
    private lateinit var flipCamera: FloatingActionButton
    private lateinit var overlay: GraphicOverlay
    private lateinit var faceGraphic: FaceRecognitionGraphic
    private lateinit var foundRecognizableBudgeView: FoundRecognizableBudgeView

    private lateinit var preview: Preview
    private lateinit var imageAnalysis: ImageAnalysis

    private var analyzerMode = AnalyzerMode.RECOGNITION
    private var cameraSelector = CameraSelector.LENS_FACING_BACK
    private var currentRotation = Surface.ROTATION_0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_recognition)

        println("Display rotation: ${windowManager.defaultDisplay.rotation}")

        if (inputParams is InputParams.Recognition && FaceRecognition.RECOGNIZABLE_FACES.isEmpty()) {
            finish()
            return
        }

        root = findViewById(R.id.root)
        previewView = findViewById(R.id.preview_view)
        takePictureButton = findViewById(R.id.take_picture)
        flipCamera = findViewById(R.id.flip_camera)
        overlay = findViewById(R.id.overlay)
        foundRecognizableBudgeView = findViewById(R.id.found_recognizable_budge)

        overlay.add(
            FaceRecognitionGraphic(this, overlay)
            .also { faceGraphic = it }
        )

        takePictureButton.setOnClickListener {
            analyzerMode = AnalyzerMode.TAKE_PICTURE
        }

        flipCamera.setOnClickListener {
            cameraSelector = 1 - cameraSelector

            updateCameraSettings()
        }

        foundRecognizableBudgeView.setOnClickListener {
            if (recognized.isNotEmpty()) {
                val recognizableBottomSheet = RecognizableBottomSheet(recognized.values.toList()) {
                    finishWithResult(it.id)
                }
                recognizableBottomSheet.show(supportFragmentManager, null)
            }
        }

        when (inputParams) {
            is InputParams.Recognition -> {
                takePictureButton.gone()

                foundRecognizableBudgeView.visible()

                root.updateConstraints {
                    connect(
                        R.id.flip_camera,
                        ConstraintLayout.LayoutParams.END,
                        ConstraintLayout.LayoutParams.PARENT_ID,
                        ConstraintLayout.LayoutParams.END,
                        0
                    )
                }
            }
            is InputParams.CaptureNewImage -> {
                takePictureButton.visible()

                foundRecognizableBudgeView.gone()
            }
        }

        updateCameraSettings()

        askPermissions.launch(arrayOf(Manifest.permission.CAMERA))
    }

    override fun onStart() {
        super.onStart()
        orientationEventListener.enable()
    }

    override fun onStop() {
        super.onStop()
        orientationEventListener.disable()
    }

    override fun onDestroy() {
        super.onDestroy()
        imageAnalysis.clearAnalyzer()
        preview.setSurfaceProvider(null)
        faceGraphic.boxData = null
    }

    private val askPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { statuses ->
        when {
            statuses.isGranted() -> {}
            else -> {
                finish()
            }
        }
    }

    private fun updateCameraSettings() {
        cameraProvider.unbindAll()

        preview = Preview.Builder().build().apply {
            setSurfaceProvider(previewView.surfaceProvider)
        }

        imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build().apply {
                setAnalyzer(analyzerExecutor, analyzer)
            }

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(cameraSelector)
            .build()

        cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis)
    }

    private fun finishWithResult(image: Bitmap, faceDataResponse: FaceDataResponse.Success) {
        tempFile.outputStream().use {
            image.compress(Bitmap.CompressFormat.JPEG, 100, it)
        }

        val intent = Intent()
        intent.putExtra(KEY_RESPONSE, RecognitionActivityResult.NewRecognizable(tempFile.toUri(), faceDataResponse.recognitionData))
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun finishWithResult(id: Long) {
        val intent = Intent()
        intent.putExtra(KEY_RESPONSE, RecognitionActivityResult.FindRecognizable(id))
        setResult(RESULT_OK, intent)
        finish()
    }

    private inner class FaceAnalyzer : ImageAnalysis.Analyzer {
        @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
        override fun analyze(imageProxy: ImageProxy) {
            try {
                if (isFinishing) return

                runBlocking(CoroutineExceptionHandler { coroutineContext, throwable ->
                    Log.e(this.toString(), "coroutineError", throwable)
                    showToast(text = throwable.toString())
                }) {
                    val image = imageProxy.image ?: return@runBlocking
                    val rotation = imageProxy.imageInfo.rotationDegrees
                    val isImageFlipped = cameraSelector == CameraSelector.LENS_FACING_FRONT

                    if (currentRotation == Surface.ROTATION_0 || currentRotation == Surface.ROTATION_180) {
                        overlay.setImageSourceInfo(image.height, image.width, isImageFlipped, naturalRotation, currentRotation)
                    } else {
                        overlay.setImageSourceInfo(image.width, image.height, isImageFlipped, naturalRotation, currentRotation)
                    }

                    val recognitionImage = RecognitionImage.MediaRecognitionImage(rotation, image)

                    when (analyzerMode) {
                        AnalyzerMode.RECOGNITION -> {
                            analyze(recognitionImage)
                        }
                        AnalyzerMode.TAKE_PICTURE -> {
                            takePicture(recognitionImage)
                            analyzerMode = AnalyzerMode.RECOGNITION
                        }
                    }
                }
            } catch (e: Exception) {
                showToast(text = e.toString())
                Log.e(this.toString(), e.toString(), e)
            } finally {
                imageProxy.close()
            }
        }


        private suspend fun analyze(recognitionImage: RecognitionImage) {
            when (inputParams) {
                is InputParams.Recognition -> {
                    val recognitionResponse = FaceRecognition.findRecognizableFace(recognitionImage)

                    val faceFromImage = FaceRecognition.getFaceFromImage(recognitionImage)

                    if (faceFromImage == null) {
                        faceGraphic.boxData = null
                    }

                    faceFromImage?.let {
                        val faceValidation = FaceRecognition.validateFace(it)
                        faceGraphic.boxData = BoxData.NewFaceData(faceFromImage, faceValidation == null)
                    }

                    withContext(Dispatchers.Main) {
                        when (recognitionResponse) {
                            is RecognitionResponse.Success -> {
                                if (recognized.contains(recognitionResponse.recognizableFace.id).not()) {
                                    recognized[recognitionResponse.recognizableFace.id] = recognitionResponse.recognizableFace
                                    foundRecognizableBudgeView.add()
                                }
                            }
                            RecognitionResponse.FaceNotFound, is RecognitionResponse.UserNotFound -> {
                                faceGraphic.boxData = null
                                foundRecognizableBudgeView.count = recognized.keys.count()
                            }
                        }
                    }
                }
                is InputParams.CaptureNewImage -> {
                    val faceFromImage = FaceRecognition.getFaceFromImage(recognitionImage)

                    if (faceFromImage == null) {
                        faceGraphic.boxData = null
                        return
                    }

                    val faceValidation = FaceRecognition.validateFace(faceFromImage)

                    withContext(Dispatchers.Main) {
                        faceGraphic.boxData = BoxData.NewFaceData(faceFromImage, faceValidation == null)
                    }
                }
            }
        }

        private suspend fun takePicture(recognitionImage: RecognitionImage.MediaRecognitionImage) = withContext(Dispatchers.Default) {
            when (val faceDataResponse = FaceRecognition.getFaceData(recognitionImage)) {
                is FaceDataResponse.Success -> {
                    showAddRecognizableDialog(recognitionImage, faceDataResponse)
                }
                is FaceDataResponse.FaceValidationError -> {
                    println(faceDataResponse.error)
                    showToast(text = faceDataResponse.error)
                }
            }
        }

        private suspend fun showAddRecognizableDialog(originalImage: RecognitionImage, faceDataResponse: FaceDataResponse.Success) {
            val previewImage = when (originalImage) {
                is RecognitionImage.BitmapRecognitionImage -> originalImage.image
                is RecognitionImage.MediaRecognitionImage -> originalImage.image.toBitmap(originalImage.rotation)
            }

            withContext(Dispatchers.Main) {
                val job = Job()

                AddRecognizableDialog(context = this@FaceRecognitionActivity,
                    previewImage = previewImage,
                    onClickListener = {
                        job.complete()
                        finishWithResult(previewImage, faceDataResponse)
                    },
                    onCancelListener = {
                        job.complete()
                    }).show()

                job.join()
            }
        }
    }

    private inner class OrientationListener : OrientationEventListener(this) {
        override fun onOrientationChanged(orientation: Int) {
            currentRotation = when (orientation) {
                in 45 until 135 -> Surface.ROTATION_270
                in 135 until 225 -> Surface.ROTATION_180
                in 225 until 315 -> Surface.ROTATION_90
                else -> Surface.ROTATION_0
            }

            imageAnalysis.targetRotation = currentRotation
        }
    }

    private enum class AnalyzerMode {
        RECOGNITION, TAKE_PICTURE
    }

    private fun Image.toBitmap(rotation: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(rotation.toFloat())

        val bitmapFromCameraImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        yuvConverter.yuvToRgb(this, bitmapFromCameraImage)

        return Bitmap.createBitmap(bitmapFromCameraImage, 0, 0, this.width, this.height, matrix, false)
    }


}