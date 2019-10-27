package com.ghostwan.sample.geofencing

import android.app.Application
import com.ghostwan.sample.geofencing.di.mainModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {
    override fun onCreate(){
        super.onCreate()
        // start Koin!
        startKoin {
            // Android context
            androidContext(this@MainApplication)
            // modules
            modules(mainModule)
        }
    }
}