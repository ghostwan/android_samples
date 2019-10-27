package com.ghostwan.sample.geofencing.ui

import com.ghostwan.sample.geofencing.data.Source
import com.ghostwan.sample.geofencing.data.Repository
import kotlinx.coroutines.*
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
        launch {
            if(repository.isHomeValueExist()) {
                view?.setIsHome(repository.isHome())
            } else {
                withContext(Dispatchers.Main) {
                    view?.askIsHome()
                }
            }
        }
    }

    override fun detachView(view: MainContract.View) {
        if (this.view == view) {
            this.view = null
        }
    }

    override fun leaveHome(source: Source) {
        launch {
            repository.setHome( false, source)
            view?.setIsHome(false)
        }
    }

    override fun enterHome(source: Source) {
        launch {
            repository.setHome( true, source)
        }
        view?.setIsHome(true)
    }

    override fun refreshEventList() {
        launch {
            view?.showEventList(repository.getEvents())
        }
    }

    override fun clearDatabase() {
        launch {
            repository.clearEvents()
            view?.showEventList(ArrayList())
            withContext(Dispatchers.Main) {
                view?.askIsHome()
            }
        }
    }
}