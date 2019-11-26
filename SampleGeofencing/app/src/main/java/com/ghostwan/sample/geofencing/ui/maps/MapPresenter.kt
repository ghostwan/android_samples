package com.ghostwan.sample.geofencing.ui.maps

import com.ghostwan.sample.geofencing.data.Repository
import com.ghostwan.sample.geofencing.data.model.Home
import com.ghostwan.sample.geofencing.utils.elseNull
import com.ghostwan.sample.geofencing.utils.ifNotNull
import com.ghostwan.sample.geofencing.utils.toLatLng
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlin.coroutines.CoroutineContext

class MapPresenter(private val repository: Repository) : MapContract.Presenter, CoroutineScope {


    private var view: MapContract.View? = null

    private companion object {
        const val MINIMUM_RADIUS = 30.0
    }

    private var radius = MINIMUM_RADIUS
    private var initialTrigger = GeofencingRequest.INITIAL_TRIGGER_EXIT

    private var job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.IO

    private var currentHome: Home? = null
    private var currentPosition: LatLng? = null

    override fun attachView(view: MapContract.View) {
        if (this.view == null) {
            this.view = view
            updateStatus()
        }
    }

    override fun updateStatus() {
        launch {
            currentHome = repository.getHomeData()

            var permission = false
            withContext(Main) {
                permission = view?.checkAndAskPermissions() ?: false
                view?.prepareMap(permission)
            }

            when {
                currentHome != null -> {
                    val home = currentHome!!
                    radius = home.radius
                    initialTrigger = home.initialTrigger
                    withContext(Main) {
                        view?.displayHomeMarker(home.latLng, radius)
                        view?.moveCamera(home.latLng)
                    }
                }
                currentHome == null && permission -> {
                    withContext(Main) {
                        radius = view?.getLastLocation()?.accuracy?.toDouble() ?: MINIMUM_RADIUS
                        if (radius < MINIMUM_RADIUS)
                            radius = MINIMUM_RADIUS
                        view?.getLastLocation()?.ifNotNull {
                            setTemporaryMarker(it.toLatLng())
                            view?.moveCamera(it.toLatLng())
                        }
                    }
                }
            }
        }
    }

    override fun detachView(view: MapContract.View) {
        this.view = null
    }

    override fun setTemporaryMarker(latLng: LatLng) {
        currentPosition = latLng
        view?.displayTmpMarker(latLng, radius)
    }

    override fun saveHomePosition() {
        launch {
            currentPosition?.ifNotNull { position ->
                currentHome?.ifNotNull { home ->
                    home.latLng = position
                    home.radius = radius
                    home.initialTrigger = initialTrigger
                    repository.saveHomeData(home)
                } ?: elseNull {
                    currentHome = repository.createHome(position, radius, initialTrigger)
                }
                withContext(Main) {
                    view?.clearTmpPosition()
                    view?.displayHomeMarker(currentHome!!.latLng, radius)
                    view?.registerGeofencing()
                }
            }
        }
    }

    override suspend fun isHome(): Boolean {
        return repository.isHome()
    }

    override fun clearTmpPosition() {
        currentPosition = null
        view?.clearTmpPosition()
    }

    override fun clearSavedPosition() {

        launch {
            currentHome?.ifNotNull {
                withContext(Main) {
                    view?.clearHomePosition()
                }
                currentHome = null
            }
        }
    }

    override fun setRadius(radius: Int) {
        this.radius = radius.toDouble()
        currentPosition?.ifNotNull { view?.displayTmpMarker(it, radius.toDouble()) }
    }

    override fun askRadius() {
        view?.displayNumberPicker(radius.toInt())
    }

    override fun setInitialTrigger(initialTrigger: Int) {
        this.initialTrigger = initialTrigger
    }

    override fun askInitialTrigger() {
        view?.displayInitialTriggerChooser(initialTrigger)
    }


}