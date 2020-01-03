package com.ghostwan.sample.geofencing.di

import androidx.room.Room
import com.ghostwan.sample.geofencing.data.Database
import com.ghostwan.sample.geofencing.data.MIGRATION_1_2
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val mainModule = module {
    single {
        Room.databaseBuilder(androidApplication(), Database::class.java, "event-db")
            .addMigrations(MIGRATION_1_2)
            .build()
    }
    single { get<Database>().eventDao() }
    single { get<Database>().homeDao() }
}

