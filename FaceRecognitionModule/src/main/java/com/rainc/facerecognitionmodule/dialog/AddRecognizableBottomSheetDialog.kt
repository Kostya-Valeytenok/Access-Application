package com.rainc.facerecognitionmodule.dialog

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import com.rainc.coroutinecore.extension.launch
import com.rainc.coroutinecore.extension.updateUISafe
import com.rainc.coroutinecore.tools.CoroutineWorker
import com.rainc.facerecognitionmodule.functions.PersonData
import com.rainc.facerecognitionmodule.databinding.DialogAddRecognizableWithButtonsBinding
import com.rainc.facerecognitionmodule.extentions.hideKeyboard
import com.rainc.facerecognitionmodule.extentions.uploadToFile
import com.rainc.facerecognitionmodule.tools.ImageCache
import com.rainc.viewbindingcore.dialog.BaseBindingBottomSheetDialogFragment
import kotlinx.coroutines.Dispatchers

/**
 * Dialog to confirm that user want to use this [previewImage]
 */
class AddRecognizableBottomSheetDialog()
    : BaseBindingBottomSheetDialogFragment<DialogAddRecognizableWithButtonsBinding>(DialogAddRecognizableWithButtonsBinding::class) {

    companion object{

        private const val KEY_PERSON_DATA_ID = "PERSON_DATA_ID"

        fun newInstance(personDataId:Int): AddRecognizableBottomSheetDialog{
            return AddRecognizableBottomSheetDialog().apply {
                arguments = bundleOf(KEY_PERSON_DATA_ID to personDataId)
            }
        }
    }

    override suspend fun DialogAddRecognizableWithButtonsBinding.onViewCreated(
        view: View,
        savedInstanceState: Bundle?
    ) {
        personView.onDialogShowRequest = { childFragmentManager }
        isDialogAddRecognizable.setOnClickListener {
            it.requestFocus()
            it.clearFocus()
            it.hideKeyboard()
        }
        val userId = kotlin.runCatching { requireArguments().getInt(KEY_PERSON_DATA_ID) }.getOrNull()
        runCatching {
            ImageCache.imageCache[userId!!]!!
        }.onSuccess { personDataCache ->
            launch(Dispatchers.Default) {
                personView.setPersonData(data = personDataCache.data)
                personView.setPhotoData(photoData = personDataCache.photo)
                personView.setUserId(userId!!)
            }
            createPersonButton.setOnClickListener {
                val fullName = fullNameEditText.text?.toString() ?: return@setOnClickListener
                CoroutineWorker.launch {
                    val personModel =  PersonData(
                        id = personDataCache.data.contentHashCode().toLong(),
                        displayName = fullName,
                        fullName = fullName,
                        data = personDataCache.data,
                        photo = personDataCache.photo
                    )
                    personModel.uploadToFile(requireContext())
                    updateUISafe { dismiss() }
                }
            }
            fullNameEditText.addTextChangedListener { text ->
                personView.setFullName(text.toString())
            }
            fullNameEditText.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) makeExpanded() else makeCollapsed()}
        }.onFailure { dismiss() }

    }
}