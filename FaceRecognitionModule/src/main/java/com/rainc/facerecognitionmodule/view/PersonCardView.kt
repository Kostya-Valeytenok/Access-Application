package com.rainc.facerecognitionmodule.view

import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.cardview.widget.CardView
import androidx.core.view.doOnDetach
import androidx.fragment.app.FragmentManager
import com.rainc.coroutinecore.extension.doWhileAttached
import com.rainc.coroutinecore.extension.updateUI
import com.rainc.facerecognitionmodule.databinding.PersonCardBinding
import com.rainc.facerecognitionmodule.databinding.PersonCardViewBinding
import com.rainc.facerecognitionmodule.functions.PersonData
import com.rainc.facerecognitionmodule.dialog.QRCodeDialog
import com.rainc.facerecognitionmodule.extentions.convertDpToPixel
import com.rainc.facerecognitionmodule.extentions.decodePersonDataFromSting
import com.rainc.facerecognitionmodule.extentions.displayMetrics
import com.rainc.facerecognitionmodule.extentions.mirrored
import com.rainc.facerecognitionmodule.tools.PersonDataSerializer.serializeForQRCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.glxn.qrgen.android.QRCode

class PersonCardView  @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
) : CardView(context, attrs, defStyleAttr) {

    private val binding = PersonCardViewBinding.inflate(LayoutInflater.from(context),  this)

    private val fullNameStateFlow = MutableStateFlow("dafault")
    private val photoDataMutableStateData = MutableStateFlow<String?>(null)
    private val personData = MutableStateFlow(FloatArray(0))
    private val userIdState = MutableStateFlow(0)

    private val qrCodeSize by lazy { (context.displayMetrics.widthPixels * 0.70).toInt() }

    var onDialogShowRequest :(() ->FragmentManager)? = null

    suspend fun setPersonData(data:FloatArray) = withContext(Dispatchers.Default){
        personData.emit(data)
    }

    suspend fun setPhotoData(photoData:String?) = withContext(Dispatchers.Default){
        photoDataMutableStateData.emit(photoData)
    }

    fun setFullName(name:String) {
        fullNameStateFlow.tryEmit(name)
    }

    suspend fun setUserId(id:Int) = withContext(Dispatchers.Default){
        userIdState.emit(id)
    }

    init{
        val tenDp = context.convertDpToPixel(10)

        setContentPadding(tenDp, tenDp, tenDp, tenDp)
        radius = context.convertDpToPixel(20).toFloat()

        doWhileAttached {
            launch(Dispatchers.Default){
                photoDataMutableStateData.filterNotNull().collect{ photoData ->
                    updateUI {  }
                    runCatching { photoData.decodePersonDataFromSting().mirrored() }
                        .onSuccess { updateUI { binding.personImagePreview.setImageBitmap(it)  } }
                    updateUI {  }
                }
            }
            launch { fullNameStateFlow.collect{ binding.personFullName.text = it } }
            launch { userIdState.collect{ binding.personId.text = it.toString() } }
            launch(Dispatchers.Default) {
                combine(userIdState,personData,fullNameStateFlow){ id, data, fullName ->
                    getPersonData(id =id.toLong(), fullName = fullName, data = data)
                }.collect{
                    runCatching { QRCode.from(it).withSize(qrCodeSize,qrCodeSize).bitmap() }
                        .onSuccess {  updateUI { binding.qrCodeView.setImageBitmap(it) } }
                        .onFailure { println(it) }
                }
            }
        }
        binding.qrCodeView.setOnClickListener {
            val fragmentManager  = onDialogShowRequest?.invoke() ?: return@setOnClickListener
            QRCodeDialog.newInstance(getPersonData()).show(fragmentManager, this.toString())
        }

        doOnDetach {
            onDialogShowRequest = null
        }
    }

    fun setPersonView(image:Bitmap){

    }

    fun setQRCode(){

    }

    private fun getPersonData(
        id:Long = userIdState.value.toLong(),
        fullName:String =fullNameStateFlow.value,
        data: FloatArray = personData.value): String {
        return PersonData(
            id = id,
            displayName = fullName,
            fullName = fullName,
            data = data,
            photo = null
        ).serializeForQRCode()
    }
}