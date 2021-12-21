package com.psu.accessapplication.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Person(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val firstName: String = "",
    val secondName: String = "",
    val personImageUrl: String = "",

    @Embedded
    val face: FaceModel

)
