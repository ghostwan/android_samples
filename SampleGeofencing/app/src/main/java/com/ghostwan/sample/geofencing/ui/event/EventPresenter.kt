package com.ghostwan.sample.geofencing.ui.event

import com.ghostwan.sample.geofencing.data.Repository
import com.ghostwan.sample.geofencing.data.Source
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class EventPresenter(private val repository: Repository) :
    EventContract.Presenter, CoroutineScope {

    private var view: EventContract.View? = null
    private var job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.IO

    override fun attachView(view: EventContract.View) {
        if (this.view == null) {
            this.view = view
            updateStatus()
        }
    }

    override fun updateStatus() {
        launch(Main) {
            if (repository.isHomeValueExist()) {
                view?.setIsHome(repository.isHome())
            } else {
                view?.askIsHome()
            }
        }
    }

    override fun detachView(view: EventContract.View) {
        if (this.view == view) {
            this.view = null
        }
    }

    override fun leaveHome(source: Source) {
        launch(Main) {
            repository.setIsHome(false, source)
            view?.setIsHome(false)
        }
    }

    override fun enterHome(source: Source) {
        launch(Main) {
            repository.setIsHome(true, source)
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