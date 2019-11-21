package com.ghostwan.sample.geofencing.analytics

import com.crashlytics.android.Crashlytics
import com.ghostwan.sample.geofencing.data.Source
import com.ghostwan.sample.geofencing.data.model.Event
import com.ghostwan.sample.geofencing.data.model.Home
import com.ghostwan.sample.geofencing.utils.ifNotNull
import com.google.android.gms.location.GeofencingRequest

class FirebaseCrashlytics : AnalyticsProviderContract {

    companion object {
        private const val SOURCE_KEY: String = "source"
        private const val EVENT_KEY: String = "event"
        private const val DATE_KEY: String = "date"
        private const val RADIUS_KEY: String = "radius"
        private const val TRIGGER_KEY: String = "trigger"
        private const val IS_GEOFENCE_REGISTER_KEY: String = "geofencing register"
    }

    override fun sendEvent(event: Event, home: Home?) {

        val exception = when {
            event.source == Source.Geofencing && event.isHome-> ComeHomeGeoReport()
            event.source == Source.Geofencing && !event.isHome-> LeftHomeGeoReport()

            event.source == Source.Tile && event.isHome-> ComeHomeTileReport()
            event.source == Source.Tile && !event.isHome-> LeftHomeTileReport()

            event.source == Source.App && event.isHome-> ComeHomeAppReport()
            event.source == Source.App && !event.isHome-> LeftHomeAppReport()

            !event.isHome-> LeftHomeReport()
            event.isHome-> ComeHomeReport()

            else -> EventReport()
        }

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
        Crashlytics.logException(exception)
    }

    override fun alreadyRegister() {
        Crashlytics.logException(AlreadyRegisterReport())
    }

    override fun registerGeofencingSucceed() {
        Crashlytics.logException(RegisteringSuccessReport())
    }

    override fun registerGeofencingFailed(exception: Exception) {
        Crashlytics.logException(RegisteringFailedReport(exception))
    }

    override fun registerGeofencingCanceled() {
        Crashlytics.logException(RegisteringCanceledReport())
    }

    open class EventReport : Throwable()
    open class ComeHomeReport : EventReport()
    open class LeftHomeReport : EventReport()
    class ComeHomeGeoReport : ComeHomeReport()
    class LeftHomeGeoReport : LeftHomeReport()
    class ComeHomeTileReport : ComeHomeReport()
    class LeftHomeTileReport : LeftHomeReport()
    class ComeHomeAppReport : ComeHomeReport()
    class LeftHomeAppReport : LeftHomeReport()
    open class GeoEventReport : Throwable()
    class AlreadyRegisterReport : GeoEventReport()
    class RegisteringSuccessReport : GeoEventReport()
    class RegisteringFailedReport(exception: Exception) : Throwable(exception)
    class RegisteringCanceledReport : GeoEventReport()
}
