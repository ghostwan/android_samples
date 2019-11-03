package com.ghostwan.sample.geofencing.ui.maps

import android.location.Location
import com.ghostwan.sample.geofencing.ui.BaseContract
import com.google.android.gms.maps.model.LatLng

interface MapContract {
    interface Presenter : BaseContract.BasePresenter {
        fun attachView(view: View)
        fun detachView(view: View)
        fun setTemporaryMarker(latLng: LatLng)
        fun saveHomePosition()
        fun clearTmpPosition()
        fun clearSavedPosition()
        suspend fun isHome(): Boolean
    }

    interface View {
        suspend fun checkAndAskPermissions(): Boolean
        suspend fun prepareMap(hasPermission: Boolean)
        fun displayHomeMarker(latLng: LatLng, radius: Double = 10.0)
        fun displayTmpMarker(latLng: LatLng, radius: Double = 10.0)
        fun moveCamera(latLng: LatLng)
        suspend fun getLastLocation(): Location?
        fun clearTmpPosition()
        fun clearHomePosition()
    }
}