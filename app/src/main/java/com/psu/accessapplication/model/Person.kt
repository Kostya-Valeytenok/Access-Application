package com.psu.accessapplication.model

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity
data class Person(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,
    val firstName: String = "",
    val secondName: String = "",
    val personImageUrl: String = "",

    @Embedded
    val face: FaceModel

) : Parcelable
