package com.rainc.firestorecore.entity

import com.rainc.firestorecore.entity.user.User


enum class SubCollection(val collectionId: String) {
    Messages("messages");

    @Suppress("UNCHECKED_CAST")
    fun getParentCollectionClass(): Class<BaseEntity> {
        return when (this) {
            Messages -> User::class.java as Class<BaseEntity>
        }
    }
}