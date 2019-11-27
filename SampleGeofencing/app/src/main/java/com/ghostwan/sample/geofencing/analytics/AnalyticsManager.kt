package com.ghostwan.sample.geofencing.analytics

import android.content.Context
import com.ghostwan.sample.geofencing.data.Preference
import com.ghostwan.sample.geofencing.data.PreferenceManager
import com.ghostwan.sample.geofencing.data.model.Event
import com.ghostwan.sample.geofencing.data.model.Home


class AnalyticsManager(val context: Context, private val preferenceManager: PreferenceManager) {

    private val providersContracts: List<AnalyticsProviderContract> =
        listOf(
            FirebaseAnalytics(context),
            FirebaseCrashlytics()
        )
    private val realtime = FirebaseRealtime()


    fun sendEvent(event: Event, home: Home?) {
        providersContracts.forEach { it.sendEvent(event, home) }
        if (preferenceManager.isTrue(Preference.AUTHENTICATED) && home != null) {
            realtime.sendEvent(event, home)
        }
    }

    fun alreadyRegister() {
        providersContracts.forEach { it.alreadyRegister() }
    }

    fun registerGeofencingSucceed(home: Home) {
        providersContracts.forEach { it.registerGeofencingSucceed() }
        if (preferenceManager.isTrue(Preference.AUTHENTICATED)) {
            realtime.saveHome(home)
        }
    }

    fun registerGeofencingFailed(exception: Exception) {
        providersContracts.forEach { it.registerGeofencingFailed(exception) }
    }

    fun registerGeofencingCanceled() {
        providersContracts.forEach { it.registerGeofencingCanceled() }
    }

    fun sendHomeData(home: Home) {
        if (preferenceManager.isTrue(Preference.AUTHENTICATED)) {
            realtime.saveHome(home)
        }
    }
}