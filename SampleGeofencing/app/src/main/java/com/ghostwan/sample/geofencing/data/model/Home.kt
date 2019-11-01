package com.ghostwan.sample.geofencing.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity
data class Home(var latLng: LatLng,
                @PrimaryKey(autoGenerate = true) val id:Long?=null)