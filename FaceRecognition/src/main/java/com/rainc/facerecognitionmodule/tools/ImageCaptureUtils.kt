package com.rainc.facerecognitionmodule.tools

import android.content.Context
import com.rainc.facerecognitionmodule.tools.StaticUtil.getAttachmentsFolder
import java.io.File

object ImageCaptureUtils {
    const val FILE_PROVIDER_AUTHORITY = "com.psu.accessapplication.provider"
    const val REQUEST_CODE_IMAGE_CAPTURE = 1
    const val REQUEST_CODE_CHOOSE_IMAGE_FROM_GALLERY = 2
    private const val IMAGE_FORMAT = ".jpg"

    fun getFileByName(context: Context, fileName: String?): File {
        return File(getImageFileParentDir(context), fileName)
    }

    private fun getImageFileParentDir(context: Context): File {
        return getAttachmentsFolder(context)
    }
}