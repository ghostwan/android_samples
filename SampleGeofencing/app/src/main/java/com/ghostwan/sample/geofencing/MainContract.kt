package com.ghostwan.sample.geofencing

import android.content.Context

interface MainContract {
    interface Presenter {
        fun attachView(view: View)
        fun detachView(view: View)
        fun leaveHome(source: Source)
        fun enterHome(source: Source)
        fun updateStatus()
    }

    interface View {
        fun setIsHome(isHome: Boolean)
        fun askIsHome()
    }

    interface Repository {
        fun isHomeValueExist(context: Context): Boolean
        fun setHome(context: Context, value: Boolean, source: Source)
        fun isHome(context: Context): Boolean
    }
}