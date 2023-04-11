package com.psu.accessapplication.items

import android.annotation.SuppressLint
import com.psu.accessapplication.R
import com.psu.accessapplication.databinding.PersonCardBinding
import com.psu.accessapplication.tools.BaseBindingItem
import com.rainc.recognitionsource.model.PersonData

class PersonItem(val model: PersonData) : BaseBindingItem<PersonCardBinding>(PersonCardBinding::class) {

    override val type: Int = R.layout.person_card
    override var identifier: Long = model.id.toLong()

    @SuppressLint("SetTextI18n")
    override fun PersonCardBinding.bind() {
        userName.text = "name: ${model.displayName}"
       // userSecondName.text = "Фамилия ${model.secondName}"
        userPhoto.setImageBitmap(model.getPreview(context))
    }

    override fun PersonCardBinding.unbind() {
    }
}
