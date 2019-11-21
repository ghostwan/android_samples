package com.ghostwan.sample.geofencing.data

import com.ghostwan.sample.geofencing.geofencing.GeofencingManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import kotlin.coroutines.CoroutineContext

class HomeManager : KoinComponent, CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.IO
    private val repository by inject<Repository>()
    private val geofencingManager by inject<GeofencingManager>()

    fun deleteHome() {
        launch {
            repository.deleteHome()
            geofencingManager.clearGeofencing()
        }
    }

    fun setIsHome(value: Boolean, source: Source) {
        launch {
            repository.setIsHome(value, source)
        }
    }

    fun forceGeofencingRegistration() {
        geofencingManager.registerGeofencing(true)
    }

}