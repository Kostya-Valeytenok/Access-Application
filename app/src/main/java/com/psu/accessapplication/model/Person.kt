package com.psu.accessapplication.model

data class Person(
    val id: String = "",
    val firstName: String = "",
    val secondName: String = "",
    private val personImageUrl: String = "",
    val face: FaceModel
)
