package com.ghostwan.sample.geofencing.data.dao

import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Update

interface DefaultDao<T> {
    @Insert
    suspend fun insert(t: T)

    @Update
    suspend fun update(t: T)

    @Delete
    suspend fun delete(t: T)

}