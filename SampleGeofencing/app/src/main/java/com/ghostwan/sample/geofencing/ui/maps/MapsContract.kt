package com.ghostwan.sample.geofencing.ui.maps

import android.location.Location
import com.google.android.gms.maps.model.LatLng

interface MapsContract {
    interface Presenter {
        fun attachView(view: View)
        fun detachView(view: View)
        fun setTemporaryMarker(latLng: LatLng)
        fun saveHomePosition()
        fun clearTmpPosition()
        fun clearSavedPosition()
    }

    interface View {
        suspend fun checkAndAskPermissions(): Boolean
        suspend fun prepareMap(hasPermission: Boolean)
        fun displayHomeMarker(latLng: LatLng)
        fun displayTmpMarker(latLng: LatLng)
        fun moveCamera(latLng: LatLng)
        suspend fun getLastLocation(): Location?
        fun clearTmpPosition()
        fun clearSavedPosition()
    }
}