package com.psu.accessapplication

import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
import com.psu.accessapplication.databinding.LoaderScreenBinding
import com.psu.accessapplication.extentions.launch
import com.psu.accessapplication.extentions.updateUI
import com.psu.accessapplication.extentions.uploadImageFromUri
import com.rainc.facerecognitionmodule.tools.mfra.model.AnalyzeResult
import com.psu.accessapplication.tools.HasContractNullable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import org.koin.androidx.viewmodel.ext.android.viewModel

open class AnalyzeActivity : AppCompatActivity(), HasContractNullable<Uri, AnalyzeResult> {

    private val viewModel:AnalyzeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bind = LoaderScreenBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.circularProgressBar.progressMax = 100f
        bind.circularProgressBar.progress = 35f
        launch(Dispatchers.Default) {
            var r = 0f
            while (this.isActive) {
                r += 7f
                updateUI { bind.circularProgressBar.startAngle = r }
                delay(9)
            }
        }

        launch(Dispatchers.Default) {
            val personImage =
                checkBitmap(
                    uri = contractInput,
                    bitmap = uploadImageFromUri(contractInput, this@AnalyzeActivity)
                )
            updateUI {
                if (personImage == null) {
                    println("personImage == null")
                    setContractResult(null)
                } else {
                    val results = viewModel.chekUser(personImage)
                    setContractResult(results)
                }
            }
        }
    }

private fun checkBitmap(uri: Uri, bitmap: Bitmap?): Bitmap? {
    if (bitmap == null) return null
    return checkRotation(uri, bitmap)
}

private fun checkRotation(uri: Uri, bitmap: Bitmap): Bitmap? {
    val ei = ExifInterface(uri.path!!)
    val orientation: Int = ei.getAttributeInt(
        ExifInterface.TAG_ORIENTATION,
        ExifInterface.ORIENTATION_NORMAL
    )
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotateImage(bitmap, 90F)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotateImage(bitmap, 180F)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotateImage(bitmap, 270F)
        else -> bitmap
    }
}

private fun rotateImage(source: Bitmap, angle: Float): Bitmap? {
    val matrix = Matrix()
    matrix.postRotate(angle)
    return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, false)
}

override fun onBackPressed() {
    Toast.makeText(applicationContext, "Analyzing In Progress", Toast.LENGTH_SHORT).show()
}
}
