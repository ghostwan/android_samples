package com.ghostwan.sample.geofencing.geofencing

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.ghostwan.sample.geofencing.R


class LocationPermissionManager(
    private val context: Context)
{
    private lateinit var requestPermissions : ActivityResultLauncher<Array<String>>
    private var listener: ResultListener?=null
    private var dialogBuilder: AlertDialog.Builder?=null

    fun init(caller: ActivityResultCaller) {
        requestPermissions = caller.registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.entries.all { it.value == true }) {
                if(dialogBuilder!= null && listener != null) {
                    checkOrAskPermission(dialogBuilder!!, listener!!)
                }
            } else {
                listener?.onPermissionsRefused()
            }
        }
    }

    interface ResultListener {
        fun onPermissionsRefused()
        fun onPermissionsAccepted(isLocationServiceEnabled: Boolean)
    }

    fun isBackgroundLocationGranted(): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                // Since Android 10 (API 29) there is a new background permission needed
                isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)
                        && isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
                        && isPermissionGranted(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
            }
            else -> {
                // For android version below Android 10 (API 28, 27 ...)
                // There is no background permission only foreground one
                return isForegroundLocationGranted()
            }
        }
    }

    fun isForegroundLocationGranted(): Boolean {
        return isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)
                && isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
    }

    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun checkLocationServiceEnabled(): Boolean {
        val manager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gps = false
        var network = false

        try {
            gps = manager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) { }

        try {
            network = manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) { }

        return gps || network
    }

    fun checkOrAskPermission(dialogBuilder: AlertDialog.Builder,
                             listener: ResultListener) {

        this.dialogBuilder = dialogBuilder
        this.listener = listener

        // Since Android 11 (API 30) background permission need to be asked in two time after foreground granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!isForegroundLocationGranted()) {
                val permList = arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                requestPermissions.launch(permList)
            } else if (!isBackgroundLocationGranted()) {
                var message = context.getString(R.string.background_location_permission_message)
                message += "${context.packageManager.backgroundPermissionOptionLabel}"
                dialogBuilder
                    .setTitle(R.string.background_location_permission_title)
                    .setMessage(message)
                    .setPositiveButton(R.string.yes) { _, _ ->
                        requestPermissions.launch(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION))
                    }
                    .setNegativeButton(R.string.no) { dialog, _ ->
                        listener.onPermissionsRefused()
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            } else {
                listener.onPermissionsAccepted(checkLocationServiceEnabled())
            }

        } else {
            if (!isBackgroundLocationGranted()) {
                // Since Android 10 (API 29) a background permission need to be asked
                // This permission can be asked at the same time
                val permList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    )
                }
                // For android version below Android 10 (API 28, 27 ...)
                // There is no background permission only foreground one
                else arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                requestPermissions.launch(permList)
            } else {
                listener.onPermissionsAccepted(checkLocationServiceEnabled())
            }
        }

    }



}