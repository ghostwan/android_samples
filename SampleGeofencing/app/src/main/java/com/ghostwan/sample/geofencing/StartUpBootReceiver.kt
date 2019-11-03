package com.ghostwan.sample.geofencing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ghostwan.sample.geofencing.geofencing.GeofencingManager
import org.koin.core.KoinComponent
import org.koin.core.inject

class StartUpBootReceiver : BroadcastReceiver(), KoinComponent {

    private val geofencingManager by inject<GeofencingManager>()

    override fun onReceive(context: Context, intent: Intent) {
        geofencingManager.registerGeofencing()
    }
}
