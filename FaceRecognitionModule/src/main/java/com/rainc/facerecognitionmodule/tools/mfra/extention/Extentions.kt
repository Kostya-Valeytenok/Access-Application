package com.rainc.facerecognitionmodule.tools.mfra.extention

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.rainc.facerecognitionmodule.R
import com.rainc.facerecognitionmodule.tools.mfra.model.FaceModel
import kotlinx.coroutines.*
import java.io.IOException
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.reflect.KClass

fun Context.getImageCompat(id: Int): Drawable? {
    return ContextCompat.getDrawable(this, id)
}

fun Context.getColorCompat(id: Int): Int {
    return ContextCompat.getColor(this, id)
}

fun PointF.distance(point: PointF): Double {
    return sqrt(((point.x - this.x) * (point.x - this.x) + (point.y - this.y) * (point.y - this.y)).toDouble())
}

fun FaceModel.compare(face: FaceModel): Double {
    var percentageOfSimilarity = 0.0
    var steps = 0
    val resultAction: (Double) -> Unit = {
        result ->
        percentageOfSimilarity += result
        steps += 1
    }
    eyesDistance.compareAttributeWith(face.eyesDistance, result = resultAction)
    rEyeAndNoseDistance.compareAttributeWith(face.rEyeAndNoseDistance, result = resultAction)
    lEyeAndNoseDistance.compareAttributeWith(face.lEyeAndNoseDistance, result = resultAction)
    noseAndMouthDistance.compareAttributeWith(face.noseAndMouthDistance, resultAction)
    mouthWidth.compareAttributeWith(face.mouthWidth, result = resultAction)
    lEyeAndMouthDistance.compareAttributeWith(face.lEyeAndMouthDistance, result = resultAction)
    rEyeAndMouthDistance.compareAttributeWith(face.rEyeAndMouthDistance, result = resultAction)
    faceWidth.compareAttributeWith(face.faceWidth, result = resultAction)
    faceHeight.compareAttributeWith(face.faceHeight, result = resultAction)

    if (steps == 0)
        return 0.0

    return 100 - abs((1 - (percentageOfSimilarity / steps))) * 100
}

private fun Double?.compareAttributeWith(value: Double?, result: (Double) -> Unit) {
    if (this != null && value != null) {
        result.invoke(findSimilarity(this, value))
    }
}

private fun findSimilarity(param1Value: Double, param2Value: Double): Double {
    return if (param1Value> param2Value) {
        return param1Value / param2Value
    } else param2Value / param1Value
}

fun uploadImageFromUri(uri: Uri?, context: Context): Bitmap? {
    var bitmap: Bitmap? = null
    try {
        context.contentResolver.openInputStream(uri!!).use { imageInputStream ->
            bitmap = BitmapFactory.decodeStream(imageInputStream)
        }
    } catch (e: IOException) {
        println(e)
        return bitmap
    }
    return bitmap
}

fun <Input, Result : Parcelable> ActivityResultContract<Input, Result?>.registerContract(activity: FragmentActivity, callback: (Result: Result) -> Unit): ActivityResultLauncher<Input> {
    return activity.registerForActivityResult(this) {
        if (it != null) {
            callback.invoke(it)
        }
    }
}

fun <Input, Result : Parcelable> ActivityResultContract<Input, Result?>.registerContract(fragment: Fragment, callback: (Result: Result) -> Unit): ActivityResultLauncher<Input> {
    return fragment.registerForActivityResult(this) {
        if (it != null) {
            callback.invoke(it)
        }
    }
}

fun <Input, Result : Parcelable> ActivityResultContract<Input, Result>.registerContractNullable(
    fragment: Fragment,
    callback: (Result: Result?) -> Unit
): ActivityResultLauncher<Input> {
    return fragment.registerForActivityResult(this, callback)
}


fun <T> T?.notNull(): Boolean {
    return this != null
}

inline fun <Binding : ViewBinding> KClass<Binding>.inflate(inflater: LayoutInflater, parent: ViewGroup?, attachToRoot: Boolean): Binding {
    return java.getMethod("inflate", LayoutInflater::class.java, ViewGroup::class.java, Boolean::class.java).invoke(null, inflater, parent, attachToRoot) as Binding
}

inline fun <Binding : ViewBinding> KClass<Binding>.bind(view: View): Binding {
    return java.getMethod("bind", View::class.java).invoke(null, view) as Binding
}

fun ImageView.loadImage(url: String) {
    if (url.isNotBlank()) {
        Glide.with(context)
            .asBitmap()
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .error(R.drawable.avatar_placeholder)
            .into(this)
    } else {
        setImageResource(R.drawable.avatar_placeholder)
    }
}

fun <T> T?.nullableToResult(): Result<T> {
    return if (this == null) Result.failure(NullPointerException())
    else Result.success(this)
}

