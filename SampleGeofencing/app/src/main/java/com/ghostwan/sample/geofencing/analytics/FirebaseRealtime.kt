package com.ghostwan.sample.geofencing.analytics

import android.os.Build
import android.util.Log
import com.ghostwan.sample.geofencing.MainApplication.Companion.TAG
import com.ghostwan.sample.geofencing.data.model.Event
import com.ghostwan.sample.geofencing.data.model.Home
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.HashMap

class FirebaseRealtime {

    private val database = FirebaseDatabase.getInstance().reference

    fun sendEvent(event: Event, home: Home) {
        FirebaseAuth.getInstance().currentUser?.let {
            val eventValues = event.toMap()
            val childUpdates = getUserInfos(it, home)
            childUpdates["/users-events/${getUserID(it)}/${Date().time}"] = eventValues
            updateDatabase(childUpdates)
        }

    }

    fun updateDatabase(childUpdates: HashMap<String, Any>) {
        database.updateChildren(childUpdates).addOnSuccessListener {
            Log.i(TAG, "Success sending data to firebase!")
        }.addOnFailureListener {
            Log.e(TAG, "Sending data failed because", it)
        }
    }

    fun saveHome(home: Home) {
        FirebaseAuth.getInstance().currentUser?.let {
            updateDatabase(getUserInfos(it, home))
        }
    }

    fun getUserInfos(user: FirebaseUser, home: Home): HashMap<String, Any> {
        val childUpdates: HashMap<String, Any> = HashMap()

        val infos = mutableMapOf<String, Any?>()
        infos["home"] = home.toMap()
        val deviceInfos = mapOf<String, Any?>(
            "manufacturer" to Build.MANUFACTURER,
            "model" to Build.MODEL,
            "version" to Build.VERSION.SDK_INT,
            "version_release" to Build.VERSION.RELEASE
        )
        infos["name"] = user.displayName
        infos["device"] = deviceInfos
        infos["udpate_date"] = Date().toString()
        childUpdates["/users/${getUserID(user)}"] = infos
        return childUpdates
    }

    fun getUserID(user: FirebaseUser) = user.email
        ?.toCharArray()
        ?.filter { it.isLetterOrDigit() }
        ?.joinToString (separator = "")
        ?: user.uid
}