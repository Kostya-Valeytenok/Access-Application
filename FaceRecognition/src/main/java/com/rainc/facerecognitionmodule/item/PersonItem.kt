package com.rainc.facerecognitionmodule.item

import androidx.fragment.app.FragmentManager
import com.rainc.facerecognitionmodule.R
import com.rainc.facerecognitionmodule.databinding.PersonItemViewBinding
import com.rainc.recognitionsource.model.PersonData
import com.rainc.viewbindingcore.item.BaseBindingItem

class PersonItem internal constructor (private val model: PersonData) : BaseBindingItem<PersonItemViewBinding>(PersonItemViewBinding::class) {
    override var identifier: Long = model.hashCode().toLong()
    override val type: Int = R.id.isPersonViewItem

    var onDialogShowRequest :(() -> FragmentManager)? = null

    override fun PersonItemViewBinding.unbind() {}

    override fun PersonItemViewBinding.bind() {
       isPersonViewItem.setPersonModel(model = model)
       isPersonViewItem.onDialogShowRequest = onDialogShowRequest
    }
}