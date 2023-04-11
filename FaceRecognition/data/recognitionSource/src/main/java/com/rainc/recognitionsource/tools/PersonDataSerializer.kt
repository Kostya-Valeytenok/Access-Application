package com.rainc.recognitionsource.tools

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.google.gson.Gson
import com.rainc.recognitionsource.model.FaceDataResponse
import com.rainc.recognitionsource.model.PersonData
import com.rainc.recognitionsource.model.PersonRecognizable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
object PersonDataSerializer: KoinComponent {

    private val serializer by inject<Gson>()
    private val transformManager: ImageTransformManager by inject()

    fun FaceDataResponse.serialize():String = serializer.toJson(this)

    fun PersonRecognizable.serialize() :String = serializer.toJson(this)

    fun PersonData.serialize() :String{
       return serializer.toJson(this)
    }

    fun PersonData.serializeForQRCode() :String{
        val buff: ByteBuffer = ByteBuffer.allocate(4 * data.size)
        for (i in data.indices) {
            val amplitude = data[i]
            buff.putFloat(amplitude)
        }
        return Base64.encodeToString(buff.array(), Base64.DEFAULT)
    }

    private fun String.decodeQRCodeDatA() :FloatArray{
        val faceDataBytes = Base64.decode(this, Base64.DEFAULT)
        return ByteBuffer.wrap(faceDataBytes).asFloatBuffer().array()
    }

    fun decodePersonDataFromSting(personData: String): PersonData {
        return serializer.fromJson(personData, PersonData::class.java)
    }

    fun Bitmap.encodeToString(): String {
        val resizeImage = transformManager.resize(this, 300)
        val COMPRESSION_QUALITY = 75
        val encodedImage: String

        val byteArrayBitmapStream = ByteArrayOutputStream()
        resizeImage.compress(
            Bitmap.CompressFormat.PNG, COMPRESSION_QUALITY,
            byteArrayBitmapStream
        )
        val b: ByteArray = byteArrayBitmapStream.toByteArray()
        encodedImage = Base64.encodeToString(b, Base64.DEFAULT)
        return encodedImage
    }

    fun decodeBitmapFromString(stringPicture: String): Bitmap {
        val decodedString = Base64.decode(stringPicture, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

}