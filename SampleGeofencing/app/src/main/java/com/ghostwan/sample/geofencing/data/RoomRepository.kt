package com.ghostwan.sample.geofencing.data

import com.ghostwan.sample.geofencing.data.dao.EventDao
import com.ghostwan.sample.geofencing.data.dao.HomeDao
import com.ghostwan.sample.geofencing.data.model.Event
import com.ghostwan.sample.geofencing.data.model.Home
import com.google.android.gms.maps.model.LatLng

class RoomRepository(private val eventDao: EventDao,
                     private val homeDao: HomeDao) : Repository {


    override suspend fun isHomeValueExist(): Boolean {
        return eventDao.getAll().isNotEmpty()
    }

    override suspend fun setHome(value: Boolean, source: Source) {
        eventDao.insert(Event(value, source))
    }

    override suspend fun isHome(): Boolean {
        val events = eventDao.getAll()
        val firstEvent = events.lastOrNull()
        return firstEvent?.isHome ?: true
    }

    override suspend fun getEvents(): List<Event> {
        return eventDao.getAll()
    }

    override suspend fun clearEvents() {
        eventDao.deleteAll()
    }

    override suspend fun setHomeData(id: Long?, latLng: LatLng) {
        if(id != null) {
            val home = homeDao.get(id)
            home.latLng = latLng
            homeDao.update(home)
        }
        else {
            val homes = homeDao.getAll()
            if(homes.isEmpty()) {
                val home = Home(latLng)
                homeDao.insert(home)
            } else {
                val home = homes[0]
                home.latLng = latLng
                homeDao.update(home)
            }
        }
    }

}