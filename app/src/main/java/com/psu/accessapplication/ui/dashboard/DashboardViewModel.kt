package com.psu.accessapplication.ui.dashboard

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.psu.accessapplication.domain.VerificationUserUseCase
import com.psu.accessapplication.model.Person
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor (val userVerification: VerificationUserUseCase) : ViewModel() {

    val photo = MutableStateFlow<Bitmap?>(null)

    suspend fun chekUser(personImage: Bitmap?): Person? = viewModelScope.async {
        if (personImage == null) return@async null
        photo.emit(null)
        var person: Person? = null
        val result = userVerification.verifyUser(personImage).firstOrNull()
        result?.onSuccess {
            photo.emit(personImage)
            person = it
        }?.onFailure { println("failed") }
        println("go next")
        return@async person
    }.await()

    fun analyzeImage(image: Bitmap?) {
        if (image == null) return
        viewModelScope.launch { userVerification.analyzeImage(image) }
    }
}
