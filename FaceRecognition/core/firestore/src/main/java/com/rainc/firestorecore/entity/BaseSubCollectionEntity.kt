package com.rainc.firestorecore.entity

import com.google.firebase.firestore.Exclude

abstract class BaseSubCollectionEntity(@get:Exclude open var parentDocumentId: String, documentId: String) :
    BaseEntity(documentId) {
   internal abstract fun subCollectionType(): SubCollection
}