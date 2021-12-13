package com.psu.accessapplication.ui.dashboard

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.psu.accessapplication.domain.VerificationUserUseCase
import com.psu.accessapplication.extentions.collectOnce
import com.psu.accessapplication.model.Person
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor (val userVerification: VerificationUserUseCase) : ViewModel() {

    val photo = MutableStateFlow<Bitmap?>(null)

    suspend fun chekUser(personImage: Bitmap): Person? = viewModelScope.async {
        photo.emit(null)
        var person: Person? = null
        userVerification.verifyUser(personImage).collectOnce {
            result ->
            println("get Result")
            result.onSuccess {
                println("person find")
                photo.emit(personImage)
                person = it
            }
            result.onFailure { println("failed") }
        }.join()
        return@async person
    }.await()

    fun analyzeImage(image: Bitmap) {
        viewModelScope.launch { userVerification.analyzeImage(image) }
    }
}
