package com.ghostwan.sample.geofencing.data

import com.ghostwan.sample.geofencing.data.model.Event
import com.google.android.gms.maps.model.LatLng

interface Repository {
    suspend fun isHomeValueExist(): Boolean
    suspend fun setHome(value: Boolean, source: Source)
    suspend fun isHome(): Boolean
    suspend fun getEvents(): List<Event>
    suspend fun clearEvents()
    suspend fun setHomeData(id: Long?=null, latLng: LatLng)
}