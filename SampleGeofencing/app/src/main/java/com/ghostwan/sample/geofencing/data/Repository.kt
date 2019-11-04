package com.ghostwan.sample.geofencing.data

import com.ghostwan.sample.geofencing.data.model.Event
import com.ghostwan.sample.geofencing.data.model.Home
import com.google.android.gms.maps.model.LatLng

interface Repository {
    suspend fun isHomeValueExist(): Boolean
    suspend fun setIsHome(value: Boolean, source: Source)
    suspend fun isHome(): Boolean
    suspend fun getEvents(): List<Event>
    suspend fun clearEvents()
    suspend fun getHomeData(id: Long? = null): Home?
    suspend fun saveHomeData(home: Home)
    suspend fun createHome(latLng: LatLng): Home
    suspend fun deleteHome(home: Home? = null)
}