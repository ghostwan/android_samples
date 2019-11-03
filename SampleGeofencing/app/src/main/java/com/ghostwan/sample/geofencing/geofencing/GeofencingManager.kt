package com.ghostwan.sample.geofencing.geofencing

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.ghostwan.sample.geofencing.MainApplication.Companion.TAG
import com.ghostwan.sample.geofencing.R
import com.ghostwan.sample.geofencing.data.Repository
import com.ghostwan.sample.geofencing.data.model.Home
import com.ghostwan.sample.geofencing.ui.maps.MapPresenter
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
    private var isRegistered = false
    private val repository by inject<Repository>()
    private val notificationManager by inject<NotificationManager>()

    private val job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.Main

    private val geofencePendingIntent: PendingIntent by lazy {
        val intent = Intent(context, GeofenceBroadcastReceiver::class.java)
        PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun registerGeofencing() {
        if (isRegistered) {
            Log.w(TAG, "Geofencing already registered! ")
            return
        }
        clearGeofencing()
        launch {
            repository.getHomeData()?.ifNotNull {
                geofencingClient.addGeofences(getGeofencingRequest(it), geofencePendingIntent)?.run {
                    addOnSuccessListener {
                        Log.i(TAG, "Geofecing successfully added!")
                        isRegistered = true
                        notificationManager.display(R.string.geofecing_registration, R.string.geofencing_added_succeed)
                    }
                    addOnFailureListener {
                        Log.e(TAG, "Geofencing added failed ", it)
                        isRegistered = false
                        notificationManager.display(
                            R.string.geofecing_registration,
                            R.string.geofencing_added_failed,
                            it.localizedMessage
                        )
                    }
                    addOnCanceledListener {
                        Log.w(TAG, "Geofencing cancelled ")
                        isRegistered = false
                        notificationManager.display(R.string.geofecing_registration, R.string.geofencing_cancelled)
                    }
                }
            } ?: elseNull {
                notificationManager.display(R.string.geofecing_registration, R.string.no_home_set)
            }
        }

    }

    fun clearGeofencing() {
        geofencingClient.removeGeofences(geofencePendingIntent)
        isRegistered = false
    }

    private fun getGeofencingRequest(home: Home): GeofencingRequest {
        val geofenceList = ArrayList<Geofence>()
        geofenceList.add(
            Geofence.Builder()
                .setRequestId(context.packageName + home.id)
                .setCircularRegion(
                    home.latLng.latitude,
                    home.latLng.longitude,
                    MapPresenter.DEFAULT_RADIUS.toFloat()
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
                    "Radius ${MapPresenter.DEFAULT_RADIUS.toFloat()} "
        )
        return GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_EXIT)
            addGeofences(geofenceList)
        }.build()
    }
}