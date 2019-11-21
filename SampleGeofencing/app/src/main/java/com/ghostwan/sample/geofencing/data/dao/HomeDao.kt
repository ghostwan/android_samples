package com.ghostwan.sample.geofencing.data.dao

import androidx.room.Dao
import androidx.room.Query
import com.ghostwan.sample.geofencing.data.model.Home

@Dao
interface HomeDao : DefaultDao<Home> {
    @Query("SELECT * FROM Home")
    suspend fun getAll(): List<Home>

    @Query("SELECT * FROM Home WHERE id=:id LIMIT 1")
    suspend fun get(id: Long): Home

    @Query("DELETE FROM Home")
    suspend fun deleteAll()

}