package com.rainc.facerecognitionmodule.tools

import android.content.Context
import com.rainc.facerecognitionmodule.functions.FaceRecognition
import com.rainc.facerecognitionmodule.functions.PersonData
import com.rainc.facerecognitionmodule.functions.Recognizable
import com.rainc.facerecognitionmodule.repository.PersonDataSource
import com.rainc.initscript.InitScript
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InitDefaultFacesDataScript internal constructor (private val context: Context) : InitScript() {
    private val faceDataSet = setOf("kanstantsin.txt", "alex.txt")

    override suspend fun script() = withContext(Dispatchers.Default) {
        val faces = mutableListOf<PersonData>()
        faceDataSet.map {
            launch {
                context.assets.open(it).use {
                    faces.add(PersonDataSerializer.decodePersonDataFromSting(String(it.readBytes())))
                }
            }
        }.joinAll()
        PersonDataSource.upload(personModels = faces.toList())
    }
}