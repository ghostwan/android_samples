package com.ghostwan.sample.geofencing.data

import com.ghostwan.sample.geofencing.data.dao.EventDao
import com.ghostwan.sample.geofencing.data.dao.HomeDao
import com.ghostwan.sample.geofencing.data.model.Event
import com.ghostwan.sample.geofencing.data.model.Home
import com.ghostwan.sample.geofencing.utils.elseNull
import com.ghostwan.sample.geofencing.utils.ifNotNull
import com.google.android.gms.maps.model.LatLng

class RoomRepository(
    private val eventDao: EventDao,
    private val homeDao: HomeDao
) : Repository {


    override suspend fun isHomeValueExist(): Boolean {
        return eventDao.getAll().isNotEmpty()
    }

    override suspend fun setIsHome(value: Boolean, source: Source) {
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

    override suspend fun getHomeData(id: Long?): Home? {
        return if (id != null) {
            homeDao.get(id)
        } else {
            homeDao.getAll().lastOrNull()
        }
    }

    override suspend fun saveHomeData(home: Home) {
        homeDao.update(home)
    }

    override suspend fun createHome(latLng: LatLng, radius: Double, initialTrigger: Int): Home {
        val home = Home(latLng, false, radius, initialTrigger)
        homeDao.insert(home)
        return home
    }

    override suspend fun deleteHome(home: Home?) {
        home?.ifNotNull {
            homeDao.delete(it)
        } ?: elseNull {
            homeDao.deleteAll()
        }
    }

}