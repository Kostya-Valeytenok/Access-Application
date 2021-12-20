package com.psu.accessapplication.tools

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.mikepenz.fastadapter.binding.AbstractBindingItem
import com.mikepenz.fastadapter.binding.BindingViewHolder
import com.psu.accessapplication.extentions.inflate
import kotlin.reflect.KClass

abstract class BaseBindingItem<Binding : ViewBinding>(private val bindingKClass: KClass<Binding>) : AbstractBindingItem<Binding>() {
    lateinit var context: Context

    abstract override var identifier: Long

    abstract fun Binding.bind()
    abstract fun Binding.unbind()

    override fun bindView(binding: Binding, payloads: List<Any>) {
        super.bindView(binding, payloads)
        context = binding.root.context
        binding.bind()
    }

    override fun unbindView(binding: Binding) {
        super.unbindView(binding)
        context = binding.root.context
        binding.unbind()
    }

    @Suppress("UNCHECKED_CAST")
    override fun createBinding(inflater: LayoutInflater, parent: ViewGroup?): Binding {
        return bindingKClass.inflate(inflater, parent, false)
    }

    override fun failedToRecycle(holder: BindingViewHolder<Binding>): Boolean {
        return true
    }
}
