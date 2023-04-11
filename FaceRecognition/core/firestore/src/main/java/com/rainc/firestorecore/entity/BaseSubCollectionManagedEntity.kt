package com.rainc.firestorecore.entity

import com.google.firebase.firestore.Exclude

abstract class BaseSubCollectionManagedEntity(parentDocumentId: String, documentId: String) :
    BaseSubCollectionEntity(parentDocumentId =parentDocumentId, documentId = documentId) {
    /**
     * Fields that can be written in the firestore
     * You must mark the field with this annotation @get:Exclude
     * to tell the firebase not to send this value to se server.
     */
    @Exclude
    abstract fun mergeFields(): List<String>
}