package com.psu.accessapplication.items

import com.psu.accessapplication.R
import com.psu.accessapplication.databinding.PersonCardBinding
import com.psu.accessapplication.extentions.loadImage
import com.psu.accessapplication.model.Person
import com.psu.accessapplication.tools.BaseBindingItem

class PersonItem(val model: Person) : BaseBindingItem<PersonCardBinding>(PersonCardBinding::class) {

    override val type: Int = R.layout.person_card
    override var identifier: Long = model.id.toLong()

    override fun PersonCardBinding.bind() {
        userName.text = model.firstName
        userSecondName.text = model.secondName
        userPhoto.loadImage(model.personImageUrl)
    }

    override fun PersonCardBinding.unbind() {

    }
}
