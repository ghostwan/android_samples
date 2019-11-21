package com.ghostwan.sample.geofencing.analytics

import android.util.Log
import com.ghostwan.sample.geofencing.MainApplication.Companion.TAG
import com.ghostwan.sample.geofencing.data.model.Event
import com.ghostwan.sample.geofencing.data.model.Home
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class FirebaseRealtime {

    private val database = FirebaseDatabase.getInstance().reference

    fun sendEvent(event: Event, home: Home) {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            val eventKey = database.child("events").push().key
            if (eventKey == null) {
                Log.w(TAG, "Couldn't get push key for posts")
                return
            }
            val eventValues = event.toMap()

            val childUpdates = HashMap<String, Any>()
            childUpdates["/homes/${user.uid}"] = home.toMap(user.displayName)
            childUpdates["/homes-events/${user.uid}/$eventKey"] = eventValues

            database.updateChildren(childUpdates).addOnSuccessListener {
                Log.i(TAG, "Success sending data to firebase!")
            }.addOnFailureListener {
                Log.e(TAG, "Sending data failed because", it)
            }
        }

    }

}