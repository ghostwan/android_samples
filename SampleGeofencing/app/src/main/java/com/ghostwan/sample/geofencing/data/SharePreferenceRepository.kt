package com.ghostwan.sample.geofencing.data

import android.content.Context
import android.content.SharedPreferences
import com.ghostwan.sample.geofencing.data.model.Event
import java.util.*
import kotlin.collections.ArrayList

class SharePreferenceRepository(val context: Context) {

    companion object {
        private const val FILE_KEY = "com.ghostwan.sample.geofencing"
        private const val IS_HOME_KEY = "isHome"
        private const val IS_HOME_SOURCE_KEY = "isHome_source"
        private const val IS_HOME_DATE = "isHome_date"
    }

    private fun getPref(): SharedPreferences {
        return context.getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE)
    }

     suspend fun isHome(): Boolean {
        return getPref().getBoolean(IS_HOME_KEY, true)
    }

     suspend fun setHome(value: Boolean, source: Source) {
        with(getPref().edit()) {
            putBoolean(IS_HOME_KEY, value)
            putString(IS_HOME_SOURCE_KEY, source.name)
            putLong(IS_HOME_DATE, Date().time)
            commit()
        }
    }

     suspend fun isHomeValueExist(): Boolean {
        return getPref().contains(IS_HOME_KEY)
    }

     suspend fun getEvents(): List<Event> {
        val list = ArrayList<Event>()
        val isHome = getPref().getBoolean(IS_HOME_KEY, true)
        val isHomeSource = Source.valueOf(getPref().getString(IS_HOME_SOURCE_KEY, Source.None.name))
        val isHomeDate = Date(getPref().getLong(IS_HOME_DATE, 0))
        list.add(Event(isHome, isHomeSource, date = isHomeDate ))
        return list
    }

     suspend fun clearEvents() {
        with(getPref().edit()) {
            clear()
            commit()
        }
    }

}