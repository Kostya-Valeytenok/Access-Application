package com.psu.accessapplication.extentions

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Handler
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.annotation.AnyThread
import androidx.annotation.DimenRes
import androidx.core.content.ContextCompat
import androidx.core.view.doOnAttach
import androidx.core.view.doOnDetach
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.psu.accessapplication.R
import com.psu.accessapplication.di.App
import com.rainc.facerecognitionmodule.tools.mfra.model.FaceModel
import kotlinx.coroutines.*
import java.io.IOException
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.reflect.KClass


fun Fragment.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
) = viewLifecycleOwner.launch(context, start, block)

fun <T> Fragment.async(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T,
): Deferred<T> = viewLifecycleOwner.async(context, start, block)

fun LifecycleOwner.launch(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit,
) = lifecycleScope.launch(context, start, block)

fun <T> LifecycleOwner.async(
    context: CoroutineContext = EmptyCoroutineContext,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> T,
) = lifecycleScope.async(context, start, block)

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

val application: Application
    get() = App.application

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

fun Context.convertPxToDp(px: Int): Int {
    return (px / resources.displayMetrics.density).roundToInt()
}

fun Context.convertDpToPixel(dp: Int): Int {
    return (dp * resources.displayMetrics.density).roundToInt()
}

fun Context.convertSpToPixel(sp: Int): Int {
    return (sp * resources.displayMetrics.scaledDensity).roundToInt()
}

fun Context.getDimension(@DimenRes dimen: Int): Float {
    return resources.getDimension(dimen)
}

fun View.doWhileAttached(block: suspend CoroutineScope.() -> Unit) {
    val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)

    doOnAttach {
        coroutineScope.launch(NoCrashCoroutineExceptionHandler, block = block)
        doOnDetach {
            coroutineScope.cancel()
        }
    }
}

val NoCrashCoroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
    println("failed")
    Log.e(
        throwable.cause.toString(),
        throwable.message,
        throwable
    )
}

@AnyThread
fun Context.showToast(text: String, duration: Int = Toast.LENGTH_LONG) {
    Handler(mainLooper).post {
        Toast.makeText(this, text, duration).show()
    }
}

fun Map<String, Boolean>.isGranted(): Boolean {
    forEach {
        if (!it.value) {
            println(it.key + " failed")
            return false
        }
    }
    return true
}

fun Bitmap.mirrored(): Bitmap {
    val matrix =  Matrix();
    matrix.preScale(-1.0f, 1.0f);
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, false)
}

