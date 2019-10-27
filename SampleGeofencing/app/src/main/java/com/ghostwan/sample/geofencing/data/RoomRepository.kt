package com.ghostwan.sample.geofencing.data

import com.ghostwan.sample.geofencing.data.dao.EventDao
import com.ghostwan.sample.geofencing.data.model.Event

class RoomRepository(private val eventDao: EventDao) : Repository {


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

}