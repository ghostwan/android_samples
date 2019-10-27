package com.ghostwan.sample.geofencing.di

import androidx.room.Room
import com.ghostwan.sample.geofencing.data.EventDatabase
import com.ghostwan.sample.geofencing.data.Repository
import com.ghostwan.sample.geofencing.data.RoomRepository
import com.ghostwan.sample.geofencing.ui.MainContract
import com.ghostwan.sample.geofencing.ui.MainPresenter
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val mainModule = module {
    single<Repository> { RoomRepository(get()) }
//    single<Repository> { SharePreferenceRepository(get()) }
    factory<MainContract.Presenter> { MainPresenter(get()) }
    single {
        Room.databaseBuilder(androidApplication(), EventDatabase::class.java, "event-db")
        .build()
    }
    single { get<EventDatabase>().eventDao() }

}

