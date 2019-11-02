package com.ghostwan.sample.geofencing.ui.maps

import com.ghostwan.sample.geofencing.data.Repository
import com.ghostwan.sample.geofencing.data.model.Home
import com.ghostwan.sample.geofencing.utils.elseNull
import com.ghostwan.sample.geofencing.utils.ifNotNull
import com.ghostwan.sample.geofencing.utils.toLatLng
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MapsPresenter(private val repository: Repository) : MapsContract.Presenter, CoroutineScope {


    private var view: MapsContract.View? = null

    private var job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.IO

    private var currentHome: Home? = null
    private var currentPosition: LatLng? = null

    override fun attachView(view: MapsContract.View) {
        if (this.view == null) {
            this.view = view
            launch(Main) {

                currentHome = repository.getHomeData()
                val permission = view.checkAndAskPermissions()
                view.prepareMap(permission)

                when {
                    currentHome != null -> {
                        view.displayHomeMarker(currentHome!!.latLng)
                        view.moveCamera(currentHome!!.latLng)
                    }
                    currentHome == null && permission -> {
                        view.getLastLocation()?.ifNotNull {
                            view.displayTmpMarker(it.toLatLng())
                            view.moveCamera(it.toLatLng())
                        }
                    }
                }
            }
        }
    }

    override fun detachView(view: MapsContract.View) {
        this.view = null
    }

    override fun setTemporaryMarker(latLng: LatLng) {
        currentPosition = latLng
        view?.displayTmpMarker(latLng)
    }

    override fun saveHomePosition() {
        launch {
            currentPosition?.ifNotNull { position ->
                currentHome?.ifNotNull { home ->
                    home.latLng = position
                    repository.saveHomeData(home)
                } ?: elseNull {
                    repository.createHome(position)
                }
            }
        }
    }

    override fun clearTmpPosition() {
        currentPosition = null
        view?.clearTmpPosition()
    }

    override fun clearSavedPosition() {

        launch(Main) {
            currentHome?.ifNotNull {
                repository.deleteHome(it)
                currentHome = null
                view?.clearSavedPosition()
            }
        }
    }


}