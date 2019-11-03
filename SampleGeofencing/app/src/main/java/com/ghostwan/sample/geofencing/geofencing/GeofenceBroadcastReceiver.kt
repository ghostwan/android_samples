package com.ghostwan.sample.geofencing.geofencing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ghostwan.sample.geofencing.MainApplication.Companion.TAG
import com.ghostwan.sample.geofencing.R
import com.ghostwan.sample.geofencing.data.Repository
import com.ghostwan.sample.geofencing.data.Source
import com.ghostwan.sample.geofencing.ui.BaseFragment
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.coroutines.CoroutineContext


class GeofenceBroadcastReceiver : BroadcastReceiver(), KoinComponent, CoroutineScope {

    private val repository by inject<Repository>()
    private val job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.Main
    private val notificationManager by inject<NotificationManager>()

    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent.hasError()) {
            val errorMessage = when (geofencingEvent.errorCode) {
                GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE -> "geofence not available"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES -> "geofence too many geofences"
                GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS -> "geofence too many pending_intents"
                else -> "geofence unknown error"
            }
            Log.e(TAG, errorMessage)
            notificationManager.display(R.string.geofecing, R.string.geofencing_error, errorMessage)
            return
        }
        val transition = geofencingEvent.geofenceTransition
        launch {
            when (transition) {
                Geofence.GEOFENCE_TRANSITION_ENTER -> {
                    Log.i(TAG, "Receiving ENTERING geofencing transition")
                    notificationManager.display(R.string.geofecing, R.string.i_am_home)
                    repository.setIsHome(true, Source.Geofencing)
                }
                Geofence.GEOFENCE_TRANSITION_EXIT -> {
                    Log.i(TAG, "Receiving EXITING geofencing transition")
                    notificationManager.display(R.string.geofecing, R.string.i_left_home)
                    repository.setIsHome(false, Source.Geofencing)
                }
                else -> {
                    Log.i(TAG, "Receiving geofencing transition not handle")
                }
            }
            LocalBroadcastManager.getInstance(context).sendBroadcast(Intent(BaseFragment.INTENT_UPDATE_STATUS))
        }
    }
}
