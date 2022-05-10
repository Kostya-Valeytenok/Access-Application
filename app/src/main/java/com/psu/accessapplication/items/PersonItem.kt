package com.psu.accessapplication.items

import android.annotation.SuppressLint
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.psu.accessapplication.R
import com.psu.accessapplication.databinding.PersonCardBinding
import com.psu.accessapplication.extentions.doWhileAttached
import com.psu.accessapplication.extentions.gone
import com.psu.accessapplication.extentions.loadImage
import com.psu.accessapplication.extentions.visible
import com.psu.accessapplication.model.Person
import com.psu.accessapplication.tools.BaseBindingItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PersonItem(val model: Person, private val loader: RequestManager) : BaseBindingItem<PersonCardBinding>(PersonCardBinding::class) {

    override val type: Int = R.layout.person_card
    override var identifier: Long = model.id.toLong()

    @SuppressLint("SetTextI18n")
    override fun PersonCardBinding.bind() {
        userName.text = "Имя: ${model.firstName}"
        userSecondName.text = "Фамилия ${model.secondName}"
        userPhoto.loadImage(model.personImageUrl)
    }

    override fun PersonCardBinding.unbind() {
    }
}
