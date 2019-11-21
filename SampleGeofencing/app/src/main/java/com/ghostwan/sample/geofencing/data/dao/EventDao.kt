package com.ghostwan.sample.geofencing.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ghostwan.sample.geofencing.data.model.Event

@Dao
interface EventDao : DefaultDao<Event> {
    @Query("SELECT * FROM Event")
    suspend fun getAll(): List<Event>

    @Query("DELETE FROM Event")
    suspend fun deleteAll()

}