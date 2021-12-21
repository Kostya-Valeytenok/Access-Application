package com.psu.accessapplication.repository.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.psu.accessapplication.model.Person
import kotlinx.coroutines.flow.Flow

@Dao
interface personDao {

    @get:Query("SELECT * FROM person")
    val allRX: Flow<List<Person>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(newPerson: Person)
}
