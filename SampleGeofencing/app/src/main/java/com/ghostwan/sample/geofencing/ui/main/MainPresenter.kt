package com.ghostwan.sample.geofencing.ui.main

import com.ghostwan.sample.geofencing.data.Source
import com.ghostwan.sample.geofencing.data.Repository
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlin.coroutines.CoroutineContext

class MainPresenter(private val repository: Repository) :
    MainContract.Presenter, CoroutineScope {

    private var view: MainContract.View? = null
    private var job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.IO

    override fun attachView(view: MainContract.View) {
        if(this.view == null) {
            this.view = view
            updateStatus()
        }
    }

    override fun updateStatus() {
        launch(Main) {
            if(repository.isHomeValueExist()) {
                view?.setIsHome(repository.isHome())
            } else {
                view?.askIsHome()
            }
        }
    }

    override fun detachView(view: MainContract.View) {
        if (this.view == view) {
            this.view = null
        }
    }

    override fun leaveHome(source: Source) {
        launch(Main) {
            repository.setHome( false, source)
            view?.setIsHome(false)
        }
    }

    override fun enterHome(source: Source) {
        launch(Main) {
            repository.setHome( true, source)
        }
        view?.setIsHome(true)
    }

    override fun refreshEventList() {
        launch(Main) {
            view?.showEventList(repository.getEvents())
        }
    }

    override fun clearDatabase() {
        launch(Main) {
            repository.clearEvents()
            view?.showEventList(ArrayList())
            view?.askIsHome()
        }
    }

    override fun setHomeLocation(latitude: Long, longitude: Long) {
    }
}