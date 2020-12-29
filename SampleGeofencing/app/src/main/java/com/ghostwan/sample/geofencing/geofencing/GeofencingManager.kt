package com.ghostwan.sample.geofencing.geofencing

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.ghostwan.sample.geofencing.MainApplication.Companion.TAG
import com.ghostwan.sample.geofencing.LocationPermissionCompat
import com.ghostwan.sample.geofencing.R
import com.ghostwan.sample.geofencing.analytics.AnalyticsManager
import com.ghostwan.sample.geofencing.data.Repository
import com.ghostwan.sample.geofencing.data.model.Home
import com.ghostwan.sample.geofencing.utils.elseNull
import com.ghostwan.sample.geofencing.utils.ifNotNull
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.coroutines.CoroutineContext

class GeofencingManager(val context: Context) : KoinComponent, CoroutineScope {

    private val geofencingClient: GeofencingClient by lazy { LocationServices.getGeofencingClient(context) }
    private val repository by inject<Repository>()
    private val notificationManager by inject<NotificationManager>()
    private val analyticsManager by inject<AnalyticsManager>()

    private val job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.IO

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    suspend fun getHome(): Home? {
        return repository.getHomeData()
    }

    @SuppressLint("MissingPermission")
    fun registerGeofencing(force: Boolean = false) {
        launch {
            if (!LocationPermissionCompat.isBackgroundLocationGranted(context)) {
                notificationManager.display(R.string.geofecing_registration, R.string.geofencing_permission_issue)
                return@launch
            }

            getHome()?.ifNotNull { home ->
                if (home.isGeofencingRegistered && !force) {
                    Log.w(TAG, "Geofencing already registered! ")
                    notificationManager.display(R.string.geofecing_registration, R.string.geofecing_already_registered)
                    analyticsManager.alreadyRegister()
                    return@launch
                }
                clearGeofencing()
                geofencingClient.addGeofences(getGeofencingRequest(home), geofencePendingIntent)?.run {
                    addOnSuccessListener {
                        Log.i(TAG, "Geofecing successfully added!")
                        setGeofencingRegistration(home, true)
                        notificationManager.display(
                            R.string.geofecing_registration,
                            R.string.geofencing_added_succeed
                        )
                        analyticsManager.registerGeofencingSucceed(home)
                    }
                    addOnFailureListener {
                        Log.e(TAG, "Geofencing added failed ", it)
                        setGeofencingRegistration(home, false)
                        notificationManager.display(
                            R.string.geofecing_registration,
                            R.string.geofencing_added_failed,
                            it.localizedMessage
                        )
                        analyticsManager.registerGeofencingFailed(it)
                    }
                    addOnCanceledListener {
                        Log.w(TAG, "Geofencing cancelled ")
                        setGeofencingRegistration(home, false)
                        notificationManager.display(R.string.geofecing_registration, R.string.geofencing_cancelled)
                        analyticsManager.registerGeofencingCanceled()
                    }
                }
            } ?: elseNull {
                notificationManager.display(R.string.geofecing_registration, R.string.no_home_set)
            }
        }

    }

    private fun setGeofencingRegistration(home: Home, value: Boolean) {
        launch {
            home.isGeofencingRegistered = value
            repository.saveHomeData(home)
        }
    }

    suspend fun clearGeofencing() {
        getHome()?.ifNotNull {
            geofencingClient.removeGeofences(geofencePendingIntent)
            setGeofencingRegistration(it, false)
        }
    }

    private fun getGeofencingRequest(home: Home): GeofencingRequest {
        val geofenceList = ArrayList<Geofence>()
        geofenceList.add(
            Geofence.Builder()
                .setRequestId(context.packageName + home.id)
                .setCircularRegion(
                    home.latLng.latitude,
                    home.latLng.longitude,
                    home.radius.toFloat()
                )
                .setTransitionTypes(
                    Geofence.GEOFENCE_TRANSITION_ENTER or
                            Geofence.GEOFENCE_TRANSITION_EXIT
                )
                .setExpirationDuration(-1)
                .build()
        )

        Log.i(
            TAG, "Add coordinates for Latitude: ${home.latLng.latitude} " +
                    "Longitude: ${home.latLng.longitude} " +
                    "Radius ${home.radius.toFloat()} "
        )
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(home.initialTrigger)
            addGeofences(geofenceList)
        }.build()
    }

}