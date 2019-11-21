package com.ghostwan.sample.geofencing.analytics

import android.content.Context
import com.ghostwan.sample.geofencing.data.model.Event
import com.ghostwan.sample.geofencing.data.model.Home


class AnalyticsManager(val context: Context) {

    private val providersContracts:List<AnalyticsProviderContract> =
        listOf(FirebaseAnalytics(context), FirebaseCrashlytics())


    fun sendEvent(event: Event, home: Home?) {
        providersContracts.forEach { it.sendEvent(event, home) }
    }

    fun alreadyRegister() {
        providersContracts.forEach { it.alreadyRegister() }
    }

    fun registerGeofencingSucceed() {
        providersContracts.forEach { it.registerGeofencingSucceed() }
    }

    fun registerGeofencingFailed(exception: Exception) {
        providersContracts.forEach { it.registerGeofencingFailed(exception) }
    }

    fun registerGeofencingCanceled() {
        providersContracts.forEach { it.registerGeofencingCanceled() }
    }
}