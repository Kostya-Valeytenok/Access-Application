package com.rainc.facerecognitionmodule.tools

object ImageCache {

    val imageCache = mutableMapOf<Int,ImageCacheItem>()

    data class ImageCacheItem(
        val data: FloatArray,
        val photo:String?
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ImageCacheItem

            if (!data.contentEquals(other.data)) return false
            if (photo != other.photo) return false

            return true
        }

        override fun hashCode(): Int {
            var result = data.contentHashCode()
            result = 31 * result + (photo?.hashCode() ?: 0)
            return result
        }
    }
}