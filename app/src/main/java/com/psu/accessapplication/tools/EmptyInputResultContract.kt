package com.psu.accessapplication.tools

import android.os.Parcelable
import androidx.fragment.app.FragmentActivity

open class EmptyInputResultContract<Result : Parcelable>(clazz: Class<out FragmentActivity>) : BaseResultContract<Unit, Result>(clazz)

open class ResultContract<Input : Parcelable, Result : Parcelable>(clazz: Class<out FragmentActivity>) :
    BaseResultContract<Input, Result>(clazz)