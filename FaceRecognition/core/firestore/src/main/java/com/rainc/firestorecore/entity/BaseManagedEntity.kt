package com.rainc.firestorecore.entity

import com.google.firebase.firestore.Exclude

abstract class BaseManagedEntity(documentId: String) : BaseEntity(documentId) {
    /**
     * Fields that can be written in the firestore
     * You must mark the field with this annotation @get:Exclude
     * to tell the firebase not to send this value to se server.
     */
    @Exclude
    abstract fun mergeFields(): List<String>
}