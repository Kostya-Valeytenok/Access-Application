package com.rainc.firestorecore.entity

import com.google.firebase.firestore.Exclude

open class BaseEntity (@get:Exclude open var documentId: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BaseEntity) return false

        if (documentId != other.documentId) return false

        return true
    }

    override fun hashCode(): Int {
        return documentId.hashCode()
    }
}