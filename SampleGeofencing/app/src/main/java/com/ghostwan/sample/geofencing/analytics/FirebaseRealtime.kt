package com.ghostwan.sample.geofencing.analytics

import android.util.Log
import com.ghostwan.sample.geofencing.MainApplication.Companion.TAG
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase


class FirebaseRealtime {

    private val database = FirebaseDatabase.getInstance().reference

    fun clearEvents() {
        FirebaseAuth.getInstance().currentUser?.let {
            val childUpdates: HashMap<String, Any> = HashMap()
            childUpdates["/users-events/${getUserID(it)}"] = ""
            childUpdates["/users/${getUserID(it)}"] = ""
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

    fun getUserID(user: FirebaseUser) = user.email
        ?.toCharArray()
        ?.filter { it.isLetterOrDigit() }
        ?.joinToString(separator = "")
        ?: user.uid
}