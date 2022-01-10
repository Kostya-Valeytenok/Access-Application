package com.psu.accessapplication.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.psu.accessapplication.model.Person
import com.psu.accessapplication.repository.dao.personDao

@Database(
    entities = [
        Person::class
    ],
    version = 27

)
abstract class AppDatabase : RoomDatabase() {
    abstract fun persons(): personDao
}
