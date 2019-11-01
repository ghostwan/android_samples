package com.ghostwan.sample.geofencing.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ghostwan.sample.geofencing.data.dao.EventDao
import com.ghostwan.sample.geofencing.data.dao.HomeDao
import com.ghostwan.sample.geofencing.data.model.Event
import com.ghostwan.sample.geofencing.data.model.Home

@Database(entities = [Event::class, Home::class], version = 1)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun homeDao(): HomeDao
}