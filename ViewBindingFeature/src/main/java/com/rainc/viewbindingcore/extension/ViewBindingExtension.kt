package com.rainc.viewbindingcore.extension

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass

inline fun <Binding : ViewBinding> KClass<Binding>.inflate(inflater: LayoutInflater, parent: ViewGroup?, attachToRoot: Boolean): Binding {
    return java.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java).invoke(null, inflater, parent, attachToRoot) as Binding
}

inline fun <Binding : ViewBinding> KClass<Binding>.bind(view: View): Binding {
    return java.getMethod("bind", View::class.java).invoke(null, view) as Binding
}