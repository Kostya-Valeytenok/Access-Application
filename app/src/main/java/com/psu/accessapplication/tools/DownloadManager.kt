package com.psu.accessapplication.tools

import android.graphics.Bitmap
import com.bumptech.glide.RequestManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadManager (private val imageLoader: RequestManager) {

    suspend fun downLoadImage(url: String): Result<Bitmap> = withContext(Dispatchers.Default) {
        return@withContext runCatching {
            imageLoader
                .asBitmap()
                .load(url)
                .submit()
                .get()
        }
    }
}
