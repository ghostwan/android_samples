package com.ghostwan.sample.geofencing.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ghostwan.sample.geofencing.data.model.Event

@Dao
interface EventDao {
    @Insert
    suspend fun insert(event: Event)

    @Delete
    suspend fun delete(event: Event)

    @Query("SELECT * FROM Event")
    suspend fun getAll(): List<Event>

    @Query("DELETE FROM Event")
    fun deleteAll()

}