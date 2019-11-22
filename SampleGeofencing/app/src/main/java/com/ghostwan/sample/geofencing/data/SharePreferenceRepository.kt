package com.ghostwan.sample.geofencing.data

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(val context: Context) {

    companion object {
        private const val FILE_KEY = "com.ghostwan.sample.geofencing"
        private const val IS_AUTHENTICATED_KEY = "IS_AUTHENTICATED_KEY"
    }

    private fun getPref(): SharedPreferences {
        return context.getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE)
    }

    fun isAuthenticated(): Boolean {
        return getPref().getBoolean(IS_AUTHENTICATED_KEY, true)
    }

    fun setIsAuthenticated(value: Boolean) {
        with(getPref().edit()) {
            putBoolean(IS_AUTHENTICATED_KEY, value)
            commit()
        }
    }

    fun isPreferenceAuthenticatedExist(): Boolean {
        return getPref().contains(IS_AUTHENTICATED_KEY)
    }

    fun clear() {
        with(getPref().edit()) {
            clear()
            commit()
        }
    }

}