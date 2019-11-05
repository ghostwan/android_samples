package com.ghostwan.sample.geofencing.utils

import com.crashlytics.android.Crashlytics
import com.ghostwan.sample.geofencing.data.model.Event
import com.ghostwan.sample.geofencing.data.model.Home
import com.google.android.gms.location.GeofencingRequest

class Analytics {
    class v1 {
        companion object {
            private const val SOURCE_KEY: String = "source"
            private const val EVENT_KEY: String = "event"
            private const val DATE_KEY: String = "date"
            private const val RADIUS_KEY: String = "radius"
            private const val TRIGGER_KEY: String = "trigger"
            private const val IS_GEOFENCE_REGISTER_KEY: String = "geofencing register"

            fun sendEvent(event: Event, home: Home?) {
                Crashlytics.setString(SOURCE_KEY, event.source.name)
                Crashlytics.setString(EVENT_KEY, if (event.isHome) "come home" else "left home")
                Crashlytics.setString(DATE_KEY, event.date.toString())
                home?.ifNotNull {
                    Crashlytics.setDouble(RADIUS_KEY, it.radius)
                    Crashlytics.setString(
                        TRIGGER_KEY,
                        if (it.initialTrigger == GeofencingRequest.INITIAL_TRIGGER_EXIT) "EXIT" else "ENTER"
                    )
                    Crashlytics.setBool(IS_GEOFENCE_REGISTER_KEY, it.isGeofencingRegistered)
                }
                Crashlytics.logException(EventReport())
            }
        }
    }

    class EventReport : Throwable()
}