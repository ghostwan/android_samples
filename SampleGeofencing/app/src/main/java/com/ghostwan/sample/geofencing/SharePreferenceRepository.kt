package com.ghostwan.sample.geofencing

import android.content.Context
import android.content.SharedPreferences

class SharePreferenceRepository : MainContract.Repository{

    companion object {
        private const val FILE_KEY = "com.ghostwan.sample.geofencing"
        private const val IS_HOME_KEY = "isHome"
    }

    private fun getPref(context: Context): SharedPreferences {
        return context.getSharedPreferences(FILE_KEY, Context.MODE_PRIVATE)
    }

    override fun isHome(context: Context): Boolean {
        return getPref(context).getBoolean(IS_HOME_KEY, true)
    }

    override fun setHome(context: Context, value: Boolean, source: Source) {
        with(getPref(context).edit()) {
            putBoolean(IS_HOME_KEY, value)
            commit()
        }
    }

    override fun isHomeValueExist(context: Context): Boolean {
        return getPref(context).contains(IS_HOME_KEY)
    }

}