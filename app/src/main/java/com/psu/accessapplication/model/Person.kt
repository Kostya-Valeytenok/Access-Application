package com.psu.accessapplication.model

data class Person(
    val id: String = "",
    val firstName: String = "",
    val secondName: String = "",
    val personImageUrl: String = "",
    val face: FaceModel
)
