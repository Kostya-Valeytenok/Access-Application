package com.rainc.recognitionsource.model

import android.content.Context
import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName
import com.rainc.recognitionsource.tools.PersonDataSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PersonData (
    @SerializedName(KEY_ID)
    @SerialName(KEY_ID)
    override val id: Long,

    @SerializedName(KEY_DISPLAY_NAME)
    @SerialName(KEY_DISPLAY_NAME)
    override val displayName: String,

    @SerializedName(KEY_FULL_NAME)
    @SerialName(KEY_FULL_NAME)
    val fullName: String,

    @SerializedName(KEY_DATA)
    @SerialName(KEY_DATA)
    override val data: FloatArray,

    @SerializedName(KEY_PHOTO)
    @SerialName(KEY_PHOTO)
    val photo:String?
) : Recognizable {

    companion object{
        const val KEY_ID = "person_id"
        const val KEY_DISPLAY_NAME = "display_name"
        const val KEY_FULL_NAME = "full_name"
        const val KEY_DATA = "person_data"
        const val KEY_PHOTO= "person_photo"
    }
    fun getPhoto():Bitmap?{
        if(photo.isNullOrBlank()) return null
        return PersonDataSerializer.decodeBitmapFromString(photo)
    }

    override fun getPreview(context: Context): Bitmap? {
       return getPhoto()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PersonData

        if (id != other.id) return false
        if (displayName != other.displayName) return false
        if (fullName != other.fullName) return false
        if (!data.contentEquals(other.data)) return false
        if (photo != other.photo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + displayName.hashCode()
        result = 31 * result + fullName.hashCode()
        result = 31 * result + data.contentHashCode()
        result = 31 * result + (photo?.hashCode() ?: 0)
        return result
    }
}
