package com.ghostwan.sample.geofencing

import android.Manifest
import android.content.Context
import android.content.Context.POWER_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat


class LocationPermissionCompat(
    private val context: Context,
    resultCaller: ActivityResultCaller,
    private val listener: Listener
) {

    companion object {
        fun isBackgroundLocationGranted(context: Context): Boolean {
            return when {
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q -> {
                    // Since Android 10 (API 29) there is a new background permission needed
                    isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)
                            && isPermissionGranted(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                            && isPermissionGranted(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                }
                else -> {
                    // For android version below Android 10 (API 28, 27 ...)
                    // There is no background permission only foreground one
                    return isForegroundLocationGranted(context)
                }
            }
        }

        fun isForegroundLocationGranted(context: Context): Boolean {
            return isPermissionGranted(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    && isPermissionGranted(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        private fun isPermissionGranted(context: Context, permission: String): Boolean {
            return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        }
    }

    interface Listener {
        fun onPermissionsRefused()
        fun onPermissionsAccepted(isLocationServiceEnabled: Boolean, isAppOptimized: Boolean)
    }

    private val requestPermissions =
        resultCaller.registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions.entries.all { it.value == true }) {
                checkAndAskPermission()
            } else {
                listener.onPermissionsRefused()
            }
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

    fun checkApplicationOptimized(): Boolean {
        // Since Android 6 (API 23) Doze it optimizing background work which could affect geofencing
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (context.getSystemService(POWER_SERVICE) as PowerManager)
                .isIgnoringBatteryOptimizations(context.packageName)
        } else {
            false
        }
    }

    fun checkAndAskPermission() {
        // Since Android 11 (API 30) background permission need to be asked in two time after foreground granted
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!isForegroundLocationGranted(context)) {
                val permList = arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
                requestPermissions.launch(permList)
            } else if (!isBackgroundLocationGranted(context)) {
                var message = context.getString(R.string.background_location_permission_message)
                message += "${context.packageManager.backgroundPermissionOptionLabel}"
                AlertDialog.Builder(context)
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
                listener.onPermissionsAccepted(checkLocationServiceEnabled(), checkApplicationOptimized())
            }

        } else {
            if (!isBackgroundLocationGranted(context)) {
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
                listener.onPermissionsAccepted(checkLocationServiceEnabled(), checkApplicationOptimized())
            }
        }

    }


}