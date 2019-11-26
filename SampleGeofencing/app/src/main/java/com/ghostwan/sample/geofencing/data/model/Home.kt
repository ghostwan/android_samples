package com.ghostwan.sample.geofencing.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@Entity
@IgnoreExtraProperties
data class Home(
    var latLng: LatLng,
    var isGeofencingRegistered: Boolean,
    var radius: Double,
    var initialTrigger: Int,
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null
) {
    @Exclude
    fun toMap(): MutableMap<String, Any?> {
        return mutableMapOf(
            "latLng" to latLng,
            "isGeofencingRegistered" to isGeofencingRegistered,
            "radius" to radius,
            "initialTrigger" to if (initialTrigger == GeofencingRequest.INITIAL_TRIGGER_EXIT) "exit" else "enter"
        )
    }
}