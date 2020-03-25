package com.ghostwan.sample.geofencing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ghostwan.sample.geofencing.data.HomeManager
import org.koin.core.KoinComponent
import org.koin.core.inject

class BootReceiver : BroadcastReceiver(), KoinComponent {

    private val homeManager by inject<HomeManager>()

    override fun onReceive(context: Context, intent: Intent) {
        homeManager.forceGeofencingRegistration()
    }
}
