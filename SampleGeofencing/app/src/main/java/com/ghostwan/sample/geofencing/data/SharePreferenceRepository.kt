package com.ghostwan.sample.geofencing.data

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(val context: Context) {

    companion object {
        private const val FILE_KEY = "com.ghostwan.sample.geofencing"
    }

    private fun getPref(): SharedPreferences {
        return context.getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE)
    }


    fun isNotExistSet(preference: Preference, value: Boolean = true): Boolean {
        val isNotExist = isNotExist(preference)
        if (isNotExist) {
            set(preference, value)
        }
        return isNotExist
    }

    fun isTrue(preference: Preference): Boolean {
        return getPref().getBoolean(preference.key, preference.defaultValue)
    }

    fun isExist(preference: Preference): Boolean {
        return getPref().contains(preference.key)
    }

    fun isNotExist(preference: Preference): Boolean {
        return !isExist(preference)
    }

    fun set(preference: Preference, value: Boolean) {
        with(getPref().edit()) {
            putBoolean(preference.key, value)
            commit()
        }
    }

    fun clear() {
        with(getPref().edit()) {
            clear()
            commit()
        }
    }
}

enum class Preference(val key: String, val defaultValue: Boolean = true) {
    AUTHENTICATED("IS_AUTHENTICATED_KEY"),
    DKMA("DEVICE_INFO", false)
}