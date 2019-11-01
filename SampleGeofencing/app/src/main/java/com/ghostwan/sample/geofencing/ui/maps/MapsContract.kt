package com.ghostwan.sample.geofencing.ui.maps

interface MapsContract {
    interface Presenter {
        fun attachView(view: View)
        fun detachView(view: View)
    }

    interface View {
        suspend fun askPermissions()
    }
}