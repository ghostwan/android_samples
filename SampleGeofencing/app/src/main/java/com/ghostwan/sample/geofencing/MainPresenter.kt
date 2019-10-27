package com.ghostwan.sample.geofencing

import android.content.Context

class MainPresenter(private val context: Context, private val repository: MainContract.Repository) : MainContract.Presenter{

    private var view: MainContract.View? = null

    override fun attachView(view: MainContract.View) {
        if(this.view == null) {
            this.view = view
            updateStatus()
        }
    }

    override fun updateStatus() {
        if(repository.isHomeValueExist(context)) {
            view?.setIsHome(repository.isHome(context))
        } else {
            view?.askIsHome()
        }
    }

    override fun detachView(view: MainContract.View) {
        if (this.view == view) {
            this.view = null
        }
    }

    override fun leaveHome(source: Source) {
        repository.setHome(context, false, source)
        view?.setIsHome(false)
    }

    override fun enterHome(source: Source) {
        repository.setHome(context, true, source)
        view?.setIsHome(true)
    }
}