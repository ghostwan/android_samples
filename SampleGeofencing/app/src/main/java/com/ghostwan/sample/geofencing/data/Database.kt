package com.ghostwan.sample.geofencing.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ghostwan.sample.geofencing.data.dao.EventDao
import com.ghostwan.sample.geofencing.data.dao.HomeDao
import com.ghostwan.sample.geofencing.data.model.Event
import com.ghostwan.sample.geofencing.data.model.Home
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.migration.Migration



@Database(entities = [Event::class, Home::class], version = 2)
@TypeConverters(Converters::class)
abstract class Database : RoomDatabase() {
    abstract fun eventDao(): EventDao
    abstract fun homeDao(): HomeDao
}

val MIGRATION_1_2: Migration = object : Migration(1, 2) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE home ADD COLUMN homeLocation TEXT")
        database.execSQL("ALTER TABLE home ADD COLUMN homeType TEXT")
    }
}