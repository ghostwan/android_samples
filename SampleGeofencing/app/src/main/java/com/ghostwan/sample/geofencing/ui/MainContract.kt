package com.ghostwan.sample.geofencing.ui

import com.ghostwan.sample.geofencing.data.Source
import com.ghostwan.sample.geofencing.data.model.Event

interface MainContract {
    interface Presenter {
        fun attachView(view: View)
        fun detachView(view: View)
        fun leaveHome(source: Source)
        fun enterHome(source: Source)
        fun updateStatus()
        fun refreshEventList()
        fun clearDatabase()
    }

    interface View {
        fun setIsHome(isHome: Boolean)
        fun askIsHome()
        fun showEventList(events: List<Event>)
    }

}