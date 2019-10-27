package com.ghostwan.sample.geofencing.data

import com.ghostwan.sample.geofencing.data.model.Event

interface Repository {
    suspend fun isHomeValueExist(): Boolean
    suspend fun setHome(value: Boolean, source: Source)
    suspend fun isHome(): Boolean
    suspend fun getEvents(): List<Event>
    suspend fun clearEvents()
}