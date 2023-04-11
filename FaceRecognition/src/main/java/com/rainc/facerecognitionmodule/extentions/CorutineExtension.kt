package com.rainc.facerecognitionmodule.extentions

import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import com.rainc.recognitionsource.model.PersonData
import com.rainc.recognitionsource.tools.PersonDataSerializer
import com.rainc.recognitionsource.tools.PersonDataSerializer.serialize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

suspend fun String.decodePersonDataFromSting(): Bitmap = withContext(Dispatchers.Default)  {
    PersonDataSerializer.decodeBitmapFromString(this@decodePersonDataFromSting)
}

suspend fun PersonData.uploadToFile(context: Context) = withContext(Dispatchers.Default){
    val data = serialize()
    val fileId = fullName.lowercase().trim().replace(" ","_")
    val dir = File("//sdcard//Download//")
    val file = File(dir, "$fileId.txt")

    file.writeText(data)

    val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    downloadManager.addCompletedDownload(
        file.name,
        file.name,
        true,
        "text/plain",
        file.getAbsolutePath(),
        file.length(),
        true
    )
}