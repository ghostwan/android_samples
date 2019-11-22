package com.ghostwan.sample.geofencing.analytics

import com.ghostwan.sample.geofencing.data.model.Event
import com.ghostwan.sample.geofencing.data.model.Home

interface AnalyticsProviderContract {
    fun sendEvent(event: Event, home: Home?)
    fun alreadyRegister()
    fun registerGeofencingSucceed()
    fun registerGeofencingFailed(exception: Exception)
    fun registerGeofencingCanceled()
}