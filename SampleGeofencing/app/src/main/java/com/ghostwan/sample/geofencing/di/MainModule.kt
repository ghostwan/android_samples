package com.ghostwan.sample.geofencing.di

import androidx.room.Room
import com.ghostwan.sample.geofencing.data.Database
import com.ghostwan.sample.geofencing.data.Repository
import com.ghostwan.sample.geofencing.data.RoomRepository
import com.ghostwan.sample.geofencing.ui.event.EventContract
import com.ghostwan.sample.geofencing.ui.event.EventPresenter
import com.ghostwan.sample.geofencing.ui.maps.MapContract
import com.ghostwan.sample.geofencing.ui.maps.MapPresenter
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val mainModule = module {
    single<Repository> { RoomRepository(get(), get()) }
    factory<EventContract.Presenter> {
        EventPresenter(
            get()
        )
    }
    factory<MapContract.Presenter> {
        MapPresenter(
            get()
        )
    }
    single {
        Room.databaseBuilder(androidApplication(), Database::class.java, "event-db")
            .build()
    }
    single { get<Database>().eventDao() }
    single { get<Database>().homeDao() }

}

