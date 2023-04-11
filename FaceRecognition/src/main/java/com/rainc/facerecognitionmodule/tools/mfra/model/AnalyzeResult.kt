package com.rainc.facerecognitionmodule.tools.mfra.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
open class AnalyzeResult : Parcelable

@Parcelize
data class Successful(val similarity:Double, val person: Person) : AnalyzeResult()

@Parcelize
data class Failure(val massage: String?) : AnalyzeResult()
