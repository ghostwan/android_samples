package com.ghostwan.sample.geofencing.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity
data class Home(
    var latLng: LatLng,
    var isGeofencingRegistered: Boolean,
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null
)