package com.ghostwan.sample.geofencing.ui.event

import com.ghostwan.sample.geofencing.data.Source
import com.ghostwan.sample.geofencing.data.model.Event
import com.ghostwan.sample.geofencing.data.model.Home
import com.ghostwan.sample.geofencing.ui.BaseContract

interface EventContract {
    interface Presenter : BaseContract.BasePresenter {
        fun attachView(view: View)
        fun detachView(view: View)
        fun leaveHome(source: Source)
        fun enterHome(source: Source)
        fun refreshEventList()
        fun clearDatabase()
        fun setHomeLocation(latitude: Long, longitude: Long)
        fun setAuthentication(isAuthenticated: Boolean)
        fun saveHome(home: Home)
    }

    interface View {
        fun setIsHome(isHome: Boolean)
        fun askIsHome()
        fun showEventList(events: List<Event>)
        fun askToLogin()
        fun logout()
        fun askHomeInformation(home: Home)
        fun showDKMA()
    }

}