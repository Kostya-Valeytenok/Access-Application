package com.psu.accessapplication.demo.functions

import android.content.Context
import android.graphics.Bitmap
import android.os.Parcelable
/**
 * Defines any object that can be recognized by the face recognition component
 */
interface Recognizable {
    val id: Long

    /**
     * Displayed to the user when selecting a face from the suggestions
     */
    val displayName: String

    /**
     * Data that will be compared to find the best match
     */
    val data: FloatArray

    /**
     * Preview image that will be visible to user when selecting a person from the suggestions
     */
    fun getPreview(context: Context): Bitmap?
}
