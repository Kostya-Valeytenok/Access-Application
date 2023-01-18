package com.psu.accessapplication




import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rainc.facerecognitionmodule.tools.mfra.domain.VerificationUserUseCase
import com.rainc.facerecognitionmodule.tools.mfra.model.AnalyzeResult
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class AnalyzeViewModel(private val userVerification: VerificationUserUseCase) : ViewModel() {

    suspend fun chekUser(personImage: Bitmap?): AnalyzeResult? = viewModelScope.async {
        if (personImage == null) return@async null
        return@async  userVerification.verifyUser(personImage).await()
    }.await()

    fun analyzeImage(image: Bitmap?) {
        if (image == null) return
        viewModelScope.launch { userVerification.analyzeImage(image) }
    }
}
