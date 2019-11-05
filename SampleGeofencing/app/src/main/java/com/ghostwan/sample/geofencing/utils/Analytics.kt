package com.ghostwan.sample.geofencing.utils

import android.content.Context
import android.os.Build
import android.os.Bundle
import com.ghostwan.sample.geofencing.data.model.Event
import com.ghostwan.sample.geofencing.data.model.Home
import com.google.android.gms.location.GeofencingRequest
import com.google.firebase.analytics.FirebaseAnalytics
import java.util.*


class Analytics(val context: Context) {

    private var firebaseAnalytics: FirebaseAnalytics = FirebaseAnalytics.getInstance(context)

    fun getDataBundle(): Bundle {
        val bundle = Bundle()
        bundle.putString("manufacturer", Build.MANUFACTURER)
        bundle.putString("model", Build.MODEL)
        bundle.putInt("version", Build.VERSION.SDK_INT)
        bundle.putString("version_release", Build.VERSION.RELEASE)
        bundle.putString("date", Date().toString())
        return bundle
    }

    fun sendEvent(event: Event, home: Home?) {
        var firebaseEvent = if (event.isHome) "come_home" else "left_home"
        firebaseEvent += "_${event.source.name}"

        val bundle = getDataBundle()
        bundle.putString("event_date", event.date.toString())

        home?.ifNotNull {
            bundle.putDouble("radius", it.radius)
            bundle.putString(
                "trigger",
                if (it.initialTrigger == GeofencingRequest.INITIAL_TRIGGER_EXIT) "EXIT" else "ENTER"
            )
            bundle.putBoolean("geofence_registered", it.isGeofencingRegistered)
        }
        firebaseAnalytics.logEvent(firebaseEvent, bundle)
    }

    fun alreadyRegister() {
        firebaseAnalytics.logEvent("geofence_already_register", getDataBundle())
    }

    fun registerGeofencingSucceed() {
        firebaseAnalytics.logEvent("geofence_register_succeed", getDataBundle())
    }

    fun registerGeofencingFailed(exception: Exception) {
        firebaseAnalytics.logEvent("geofence_register_failed", getDataBundle().apply {
            putString("error_message", exception.message)
        })
    }

    fun registerGeofencingCanceled() {
        firebaseAnalytics.logEvent("geofence_register_canceled", getDataBundle())
    }
}