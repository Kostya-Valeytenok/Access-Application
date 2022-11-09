package com.psu.accessapplication.demo.functions

import android.content.Context
import android.graphics.Bitmap
import com.google.gson.annotations.SerializedName
import com.psu.accessapplication.tools.PersonDataSerializer

data class PersonData (
    @SerializedName("person_id")
    override val id: Long,
    @SerializedName("display_name")
    override val displayName: String,
    @SerializedName("full_name")
    val fullName: String,
    @SerializedName("person_data")
    override val data: FloatArray,
    @SerializedName("person_photo")
    val photo:String?
) : Recognizable {

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
