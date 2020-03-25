package com.ghostwan.sample.geofencing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.ghostwan.sample.geofencing.MainApplication.Companion.TAG
import com.ghostwan.sample.geofencing.data.HomeManager
import org.koin.core.KoinComponent
import org.koin.core.inject

class UpdatedReceiver : BroadcastReceiver(), KoinComponent {

    private val homeManager by inject<HomeManager>()

    override fun onReceive(context: Context, intent: Intent) {
        Log.d(TAG , "package replaced detected! ")
        homeManager.forceGeofencingRegistration()
    }
}
