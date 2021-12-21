package com.psu.accessapplication.tools

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

abstract class AbstractPreferences(private val name: String) {
    companion object {
        @SuppressLint("StaticFieldLeak")
        internal lateinit var context: Context

        fun init(context: Context) {
            this.context = context
        }
    }

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(name, Context.MODE_PRIVATE)
    }

    fun clear() {
        sharedPreferences.edit { clear() }
    }

    @Suppress("UNCHECKED_CAST")
    protected fun <T> getValue(key: String, defaultValue: T?): T? {
        val value = sharedPreferences.all[key]
        return if (value != null) {
            value as T
        } else {
            setValue(key, defaultValue)
            defaultValue
        }
    }

    protected fun <T> setValue(key: String, value: T?) {
        sharedPreferences.edit {
            if (value == null) {
                remove(key)
                return@edit
            }

            when (value) {
                is String -> putString(key, value)
                is Boolean -> putBoolean(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                is Int -> putInt(key, value)
                is Collection<*> -> putStringSet(key, value.map { it.toString() }.toSet())
            }
        }
    }

    protected inner class PreferencesDelegate<T>(private val key: Any, private val defaultValue: T) : ReadWriteProperty<Any, T> {
        private var cache: T? = null

        override fun getValue(thisRef: Any, property: KProperty<*>): T {
            return cache ?: getValue(key.toString(), defaultValue)!!.also {
                cache = it
            }
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
            cache = value
            setValue(key.toString(), value)
        }
    }

    protected inner class PreferencesDelegateNullable<T>(private val key: Any, private val defaultValue: T?) : ReadWriteProperty<Any, T?> {
        override fun getValue(thisRef: Any, property: KProperty<*>): T? {
            return getValue(key.toString(), defaultValue)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
            setValue(key.toString(), value)
        }
    }
}
