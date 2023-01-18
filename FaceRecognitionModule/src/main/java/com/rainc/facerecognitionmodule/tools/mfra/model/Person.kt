package com.rainc.facerecognitionmodule.tools.mfra.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Person(

    var id: Long = 0,
    val firstName: String = "",
    val secondName: String = "",
    val personImageUrl: String = "",
    val face: FaceModel

) : Parcelable
