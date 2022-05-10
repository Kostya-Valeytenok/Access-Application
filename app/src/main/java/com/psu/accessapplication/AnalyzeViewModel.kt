package com.psu.accessapplication




import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.psu.accessapplication.domain.VerificationUserUseCase
import com.psu.accessapplication.model.AnalyzeResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyzeViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var userVerification: VerificationUserUseCase


    suspend fun chekUser(personImage: Bitmap?): AnalyzeResult? = viewModelScope.async {
        if (personImage == null) return@async null
        return@async  userVerification.verifyUser(personImage).await()
    }.await()

    fun analyzeImage(image: Bitmap?) {
        if (image == null) return
        viewModelScope.launch { userVerification.analyzeImage(image) }
    }
}
