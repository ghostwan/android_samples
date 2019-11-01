package com.ghostwan.sample.geofencing.di

import androidx.room.Room
import com.ghostwan.sample.geofencing.data.Database
import com.ghostwan.sample.geofencing.data.Repository
import com.ghostwan.sample.geofencing.data.RoomRepository
import com.ghostwan.sample.geofencing.ui.main.MainContract
import com.ghostwan.sample.geofencing.ui.main.MainPresenter
import com.ghostwan.sample.geofencing.ui.maps.MapsContract
import com.ghostwan.sample.geofencing.ui.maps.MapsPresenter
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val mainModule = module {
    single<Repository> { RoomRepository(get(), get()) }
    factory<MainContract.Presenter> {
        MainPresenter(
            get()
        )
    }
    factory<MapsContract.Presenter> {
        MapsPresenter(
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

