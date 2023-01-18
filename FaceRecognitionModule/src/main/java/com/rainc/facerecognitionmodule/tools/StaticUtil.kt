package com.rainc.facerecognitionmodule.tools

import android.content.Context
import java.io.File

object StaticUtil {

    const val ATTACHMENTS_PATH = "attachments"
    @JvmStatic
    fun getAttachmentsFolder(context: Context): File {
        val file = File(context.filesDir, ATTACHMENTS_PATH)
        if (!file.exists()) {
            file.mkdir()
        }
        return file
    }

}