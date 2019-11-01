package com.ghostwan.sample.geofencing.ui.maps

import com.ghostwan.sample.geofencing.data.Repository
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlin.coroutines.CoroutineContext

class MapsPresenter(private val repository: Repository) : MapsContract.Presenter, CoroutineScope {

    private var view: MapsContract.View? = null

    private var job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.IO

    override fun attachView(view: MapsContract.View) {
        if (this.view == null) {
            this.view  = view
            launch(Main) {
                view.askPermissions()
            }
        }
    }

    override fun detachView(view: MapsContract.View) {
        this.view = null
    }
}