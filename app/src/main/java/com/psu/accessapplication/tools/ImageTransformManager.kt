package com.psu.accessapplication.tools

import android.graphics.Bitmap
import android.graphics.PointF
import com.bumptech.glide.RequestManager
import com.psu.accessapplication.extentions.asyncJob
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.coroutineScope
import kotlin.math.abs

class ImageTransformManager(private val imageLoader: RequestManager) {

    private suspend fun Bitmap.resizeImage(size: Int = 200): Result<Bitmap> {
        return asyncJob {
            return@asyncJob runCatching {
                resize(image = this, newSize = 200)
            }
        }.await()
    }

    fun resize(image: Bitmap, newSize:Int): Bitmap {
        return imageLoader
            .asBitmap()
            .load(image)
            .fitCenter()
            .override(newSize)
            .submit().get()
    }

    suspend fun transformImageToFaceImage(personPhoto: Bitmap, points: MutableList<PointF>): Result<Bitmap> = coroutineScope {
        val sortedByYListJob = points.getLastAddFirstValueAsync(sortedAction = { it -> it.sortedBy { it.y } })
        val sortedByXListJob = points.getLastAddFirstValueAsync(sortedAction = { it -> it.sortedBy { it.x } })
        val yMaxMinValues = sortedByYListJob.await()
        val xMaxMinValues = sortedByXListJob.await()
        val newImage = runCatching {
            createNewImageByParams(personPhoto, xMaxMinValues.correct(), yMaxMinValues.correct())
        }
        newImage.onSuccess {
            return@coroutineScope it.resizeImage()
        }.onFailure {
            val image = createNewImageByParams(
                personPhoto, xMaxMinValues.correct(0.01),
                yMaxMinValues.correct(0.01)
            )
            return@coroutineScope image.resizeImage()
        }
    }

    private fun createNewImageByParams(oldImage: Bitmap, xMaxMinValues: Pair<PointF, PointF>, yMaxMinValues: Pair<PointF, PointF>): Bitmap {
        return Bitmap.createBitmap(
            oldImage,
            abs(xMaxMinValues.first.x.toInt()),
            abs(yMaxMinValues.first.y.toInt()),
            xMaxMinValues.second.x.toInt() - xMaxMinValues.first.x.toInt(),
            yMaxMinValues.second.y.toInt() - yMaxMinValues.first.y.toInt()
        )
    }

    suspend inline fun MutableList<PointF>.getLastAddFirstValueAsync(
        crossinline sortedAction: (MutableList<PointF>) -> List<PointF> = { this }
    ): Deferred<Pair<PointF, PointF>> {
        return asyncJob {
            val sortedList = sortedAction.invoke(this@getLastAddFirstValueAsync)
            return@asyncJob Pair(
                sortedList.first(),
                sortedList.last()
            )
        }
    }

    fun Pair<PointF, PointF>.correct(modifier: Double = 0.1) = apply {
        var yMof = (second.y - first.y) * modifier
        var xMof = (second.x - first.x) * modifier
        if (second.x <xMof) xMof = second.x * 0.8
        if (first.x <xMof) xMof = first.x * 0.8
        if (second.y <yMof) yMof = second.y * 0.8
        if (first.y <yMof) yMof = first.y * 0.8
        with(first) {
            x -= xMof.toFloat()
            y -= yMof.toFloat()
        }
        with(second) {
            x += xMof.toFloat()
            y += yMof.toFloat()
        }
    }
}
