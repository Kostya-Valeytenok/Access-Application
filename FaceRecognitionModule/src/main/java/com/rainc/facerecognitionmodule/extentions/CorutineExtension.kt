package com.rainc.facerecognitionmodule.extentions

import android.app.DownloadManager
import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.view.View
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.rainc.facerecognitionmodule.functions.PersonData
import com.rainc.facerecognitionmodule.tools.PersonDataSerializer
import com.rainc.facerecognitionmodule.tools.PersonDataSerializer.serialize
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

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