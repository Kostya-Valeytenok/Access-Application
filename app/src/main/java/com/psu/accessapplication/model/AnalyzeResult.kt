package com.psu.accessapplication.model

import android.os.Parcelable
import com.rainc.facerecognitionmodule.tools.mfra.model.AnalyzeResult
import com.rainc.facerecognitionmodule.tools.mfra.model.Person
import kotlinx.parcelize.Parcelize

@Parcelize
open class AnalyzeResult : Parcelable

@Parcelize
data class Successful(val similarity:Double, val person: Person) : AnalyzeResult()

@Parcelize
data class Failure(val massage: String?) : AnalyzeResult()
