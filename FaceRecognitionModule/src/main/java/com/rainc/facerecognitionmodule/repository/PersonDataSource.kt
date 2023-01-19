package com.rainc.facerecognitionmodule.repository

import com.rainc.coroutinecore.tools.CoroutineWorker
import com.rainc.facerecognitionmodule.functions.FaceRecognition
import com.rainc.facerecognitionmodule.functions.PersonData
import com.rainc.facerecognitionmodule.item.PersonItem
import com.rainc.viewbindingcore.item.SpaceItem
import com.rainc.viewbindingcore.tools.Dimension
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object PersonDataSource {

    private val personDataStateFlow = MutableStateFlow<List<PersonData>>(emptyList())
    private val personDataViewStateFlow = MutableStateFlow<List<PersonItem>>(emptyList())
    val personDataViewState:StateFlow<List<PersonItem>>
        get() = personDataViewStateFlow

    init {
        CoroutineWorker.launch {
            personDataStateFlow.collect{ date ->
                FaceRecognition.RECOGNIZABLE_FACES = date
                personDataViewStateFlow.emit(date.map { PersonItem(model = it) })
            }
        }
    }

    internal suspend fun upload(personModels:List<PersonData>){
        personDataStateFlow.emit(personModels)
    }
}