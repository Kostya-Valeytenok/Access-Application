package com.psu.accessapplication.extentions

import android.content.Context
import android.graphics.PointF
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.psu.accessapplication.model.FaceModel
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.math.abs
import kotlin.math.sqrt

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
    eyesDistance.compareAttribute(face.eyesDistance, resultAction)
    rEyeAndNoseDistance.compareAttribute(face.rEyeAndNoseDistance, resultAction)
    lEyeAndNoseDistance.compareAttribute(face.lEyeAndNoseDistance, resultAction)
    noseAndMouseDistance.compareAttribute(face.noseAndMouseDistance, resultAction)
    mouthWidth.compareAttribute(face.mouthWidth, resultAction)
    lEyeAndMouseDistance.compareAttribute(face.lEyeAndMouseDistance, resultAction)
    rEyeAndMouseDistance.compareAttribute(face.rEyeAndMouseDistance, resultAction)
    if (steps == 0)
        return 0.0
    return 100 - abs((1 - (percentageOfSimilarity / steps))) * 100
}

private fun Double?.compareAttribute(value: Double?, result: (Double) -> Unit) {
    if (this != null && value != null) {
        result.invoke(findSimilarity(this, value))
    }
}

private fun findSimilarity(param1Value: Double, param2Value: Double): Double {
    return if (param1Value> param2Value) {
        return param1Value / param2Value
    } else param2Value / param1Value
}
