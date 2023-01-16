package com.psu.accessapplication.tools.initializer

import android.content.Context
import com.psu.accessapplication.demo.functions.FaceRecognition
import com.psu.accessapplication.demo.functions.Recognizable
import com.psu.accessapplication.tools.PersonDataSerializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InitDefaultFacesDataScript(private val context: Context) : InitScript() {
    private val faceDataSet = setOf("kanstantsin.txt", "alex.txt")

    override suspend fun script() = withContext(Dispatchers.Default) {
        val faces = mutableListOf<Recognizable>()
        faceDataSet.map {
            launch {
                context.assets.open(it).use {
                    faces.add(PersonDataSerializer.decodePersonDataFromSting(String(it.readBytes())))
                }
            }
        }.joinAll()
        FaceRecognition.RECOGNIZABLE_FACES = faces
    }
}