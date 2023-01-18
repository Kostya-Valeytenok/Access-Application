package com.psu.accessapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import com.psu.accessapplication.tools.FileProviderManager
import com.psu.accessapplication.tools.HasContract
import com.yalantis.ucrop.UCrop
import java.io.File

class ChoosePictureActivity : AppCompatActivity(), HasContract<Parcelable, Uri> {
    companion object {
        const val CODE_IMAGE_PICKER = 1
    }

    private val croppedImageDestination: Uri by lazy { File(cacheDir, "tempImage").toUri() }

    private val askPermissions = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { statuses ->
        when {
            statuses.isGranted() -> {
                openImageIntent()
            }
            else -> {
                finish()
            }
        }
    }

    private fun Map<String, Boolean>.isGranted(): Boolean {
        forEach {
            if (!it.value) {
                println(it.key + " failed")
                return false
            }
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = null
        askPermissions.launch(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
            )
        )
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CODE_IMAGE_PICKER) {
            var uri = data?.data

            if (uri == null && resultCode == RESULT_OK) {
                uri = FileProviderManager.imageUri
            }

            if (uri != null) {
                cropImage(uri)
            } else {
                finish()
            }
        } else if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                try {
                    val resultUri = UCrop.getOutput(data!!)!!

                    setContractResult(resultUri)
                    return
                } catch (e: Exception) {
                }
            }

            finish()
        }
    }

    private fun cropImage(uri: Uri) {
        UCrop.of(uri, croppedImageDestination)
            .withAspectRatio(1F, 1F)
            .withMaxResultSize(800, 800)
            .withOptions(
                UCrop.Options().apply {
                    setCompressionQuality(80)
                    setCircleDimmedLayer(false)
                    setShowCropFrame(true)
                    setShowCropGrid(true)
                }
            )
            .start(this)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun openImageIntent() {
        // Camera.
        val cameraIntents: MutableList<Intent> = ArrayList()
        val captureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val packageManager = packageManager
        val listCam = packageManager.queryIntentActivities(captureIntent, 0)
        for (res in listCam) {
            val packageName = res.activityInfo.packageName
            val intent = Intent(captureIntent)
            val dogImageUri = FileProviderManager.imageUri

            intent.component = ComponentName(packageName, res.activityInfo.name)
            intent.setPackage(packageName)
            intent.data = dogImageUri
            intent.flags = FLAG_GRANT_READ_URI_PERMISSION or FLAG_GRANT_WRITE_URI_PERMISSION
            intent.putExtra(MediaStore.EXTRA_OUTPUT, dogImageUri)
            cameraIntents.add(intent)
        }

        // Filesystem.
        val pickerIntent = Intent()
        pickerIntent.type = "image/*"
        pickerIntent.action = Intent.ACTION_GET_CONTENT

        // Chooser of filesystem options.
        var chooserIntent = Intent.createChooser(pickerIntent, "Select sours")

        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toTypedArray())

        // On Xiaomi Mi 10 (Android 11) createChooser doesn't work as expected
        if (Build.MANUFACTURER == "Xiaomi" && Build.VERSION.SDK_INT == 30) {
            chooserIntent = Intent().apply {
                type = "image/*"
                action = Intent.ACTION_GET_CONTENT
            }
        }

        startActivityForResult(chooserIntent, CODE_IMAGE_PICKER)
    }
}
