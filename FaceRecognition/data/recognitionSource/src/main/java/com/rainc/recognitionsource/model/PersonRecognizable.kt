package com.rainc.recognitionsource.model

import android.content.Context
import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName

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
        return preview
    }

    override fun toString(): String {
        return "PersonRecognizable{id=$id, displayName='$displayName', filename='$filename'}"
    }
}