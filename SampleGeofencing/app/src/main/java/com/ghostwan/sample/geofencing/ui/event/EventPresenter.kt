package com.ghostwan.sample.geofencing.ui.event

import com.ghostwan.sample.geofencing.data.PreferenceManager
import com.ghostwan.sample.geofencing.data.Repository
import com.ghostwan.sample.geofencing.data.Source
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Main
import kotlin.coroutines.CoroutineContext

class EventPresenter(private val repository: Repository, private val preferenceManager: PreferenceManager) :
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
        launch {
            if (!preferenceManager.isPreferenceAuthenticatedExist()) {
                withContext(Main) {
                    view?.askToLogin()
                }
            }
            if (repository.isHomeValueExist()) {
                val isHome = repository.isHome()
                withContext(Main) {
                    view?.setIsHome(isHome)
                }
            } else {
                withContext(Main) {
                    view?.askIsHome()
                }
            }
        }
    }

    override fun detachView(view: EventContract.View) {
        if (this.view == view) {
            this.view = null
        }
    }

    override fun leaveHome(source: Source) {
        launch {
            repository.setIsHome(false, source)
            withContext(Main) {
                view?.setIsHome(false)
            }
        }
    }

    override fun enterHome(source: Source) {
        launch {
            repository.setIsHome(true, source)
            withContext(Main) {
                view?.setIsHome(true)
            }
        }
    }

    override fun refreshEventList() {
        launch {
            val events = repository.getEvents()
            withContext(Main) {
                view?.showEventList(events)
            }
        }
    }

    override fun clearDatabase() {
        launch {
            repository.clearEvents()
            withContext(Main) {
                view?.showEventList(ArrayList())
                view?.askIsHome()
            }
        }
    }

    override fun setHomeLocation(latitude: Long, longitude: Long) {
    }
}