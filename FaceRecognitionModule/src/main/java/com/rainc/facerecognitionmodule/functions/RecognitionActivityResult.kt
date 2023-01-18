package com.rainc.facerecognitionmodule.functions

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Represents output of [FaceRecognitionActivity]
 */
sealed class RecognitionActivityResult : Parcelable {
    @Parcelize
    class NewRecognizable(val picturePath: Uri, val data: FloatArray) : RecognitionActivityResult()

    @Parcelize
    class FindRecognizable(val recognizableId: Long) : RecognitionActivityResult()
}

