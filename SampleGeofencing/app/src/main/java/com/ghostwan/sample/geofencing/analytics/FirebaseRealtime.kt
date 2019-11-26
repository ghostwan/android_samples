package com.ghostwan.sample.geofencing.analytics

import android.os.Build
import android.util.Log
import com.ghostwan.sample.geofencing.MainApplication.Companion.TAG
import com.ghostwan.sample.geofencing.data.model.Event
import com.ghostwan.sample.geofencing.data.model.Home
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase

class FirebaseRealtime {

    private val database = FirebaseDatabase.getInstance().reference

    fun sendEvent(event: Event, home: Home) {
        FirebaseAuth.getInstance().currentUser?.let {
            val eventKey = database.child("events").push().key
            if (eventKey == null) {
                Log.w(TAG, "Couldn't get push key for posts")
                return
            }
            val eventValues = event.toMap()

            val childUpdates = getHomeInfos(it, home)
            childUpdates["/homes-events/${it.uid}/$eventKey"] = eventValues
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
            updateDatabase(getHomeInfos(it, home))
        }
    }

    fun getHomeInfos(user: FirebaseUser, home: Home): HashMap<String, Any> {
        val childUpdates: HashMap<String, Any> = HashMap()
        
        val userInfos = home.toMap()
        val deviceInfos = mapOf<String, Any?>(
            "manufacturer" to Build.MANUFACTURER,
            "model" to Build.MODEL,
            "version" to Build.VERSION.SDK_INT,
            "version_release" to Build.VERSION.RELEASE
        )
        userInfos["name"] = user.displayName
        userInfos["device"] = deviceInfos
        childUpdates["/homes/${user.uid}"] = userInfos
        return childUpdates
    }

}