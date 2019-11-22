package com.ghostwan.sample.geofencing.utils

import android.location.Location
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

inline fun <T, R> T.ifNotNull(block: (T) -> R): R = this.let(block)
inline fun <T, R> T.elseNull(block: T.() -> R): R = this.run(block)

fun Location.toLatLng(): LatLng {
    return LatLng(this.latitude, this.longitude)
}

suspend fun awaitCallback(block: (OnMapReadyCallback) -> Unit): GoogleMap =
    suspendCoroutine { cont ->
        block(OnMapReadyCallback { googleMap -> cont.resume(googleMap) })
    }


suspend fun SupportMapFragment.getGoogleMap(): GoogleMap =
    withContext(Dispatchers.Main) {
        return@withContext awaitCallback { block ->
            this@getGoogleMap.getMapAsync(block)
        }
    }