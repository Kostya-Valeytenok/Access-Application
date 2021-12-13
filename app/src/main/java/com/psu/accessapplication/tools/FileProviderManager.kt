package com.psu.accessapplication.tools

import android.net.Uri
import androidx.core.content.FileProvider
import com.psu.accessapplication.BuildConfig
import com.psu.accessapplication.extentions.application
import java.io.File

object FileProviderManager {
    val imageUri: Uri
        get() {
            return takePictureFileUri
        }

    private const val authority = BuildConfig.APPLICATION_ID + ".provider"

    private val tempImagesFolder: File by lazy {
        File(application.cacheDir, "images").apply {
            mkdirs()
        }
    }

    private val takePictureFileUri by lazy {
        FileProvider.getUriForFile(
            application, authority,
            File(tempImagesFolder, "temp.jpg").apply {
                createNewFile()
            }
        )
    }
}
