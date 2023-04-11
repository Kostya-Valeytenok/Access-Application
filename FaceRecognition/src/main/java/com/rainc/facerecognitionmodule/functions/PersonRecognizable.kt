package com.rainc.facerecognitionmodule.functions

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.gson.annotations.SerializedName
import com.rainc.facerecognitionmodule.R
import com.rainc.facerecognitionmodule.tools.ImageCaptureUtils

/**
 * Recognizable implementation for farmers
 */
class PersonRecognizable(
    @SerializedName("person_id")
    override val id: Long,
    @SerializedName("display_name")
    override val displayName: String,
    @SerializedName("display_name")
    private val filename: String,
    override val data: FloatArray
) : Recognizable {
    private var preview: Bitmap? = null

    init {
        checkNotNull(data) { "PersonRecognizable object must contain recognition data" }
    }

    override fun getPreview(context: Context): Bitmap? {
        if (preview == null) {
            preview = try {
                BitmapFactory.decodeFile(ImageCaptureUtils.getFileByName(context, filename).path)
            } catch (e: Exception) {
                Log.e(this.toString(), "getPreview error", e)
                BitmapFactory.decodeResource(context.resources, R.drawable.ic_baseline_person_24)
            }
        }
        return preview
    }

    override fun toString(): String {
        return "PersonRecognizable{id=$id, displayName='$displayName', filename='$filename'}"
    }
}