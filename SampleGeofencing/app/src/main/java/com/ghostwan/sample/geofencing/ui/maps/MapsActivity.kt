package com.ghostwan.sample.geofencing.ui.maps

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.coroutinespermission.PermissionManager
import com.ghostwan.sample.geofencing.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import org.koin.android.ext.android.inject


class MapsActivity : AppCompatActivity(), MapsContract.View {


    companion object {
        const val EXTRA_LATITUDE = "EXTRA_LATITUDE"
        const val EXTRA_LONGITUDE = "EXTRA_LONGITUDE"
    }

    private val mapFragment by lazy { supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment }
    private var currentMarker: Marker? = null

    private val presenter by inject<MapsContract.Presenter>()
    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView(this)
    }

    override suspend fun askPermissions() {
        val result = PermissionManager.requestPermissions(
            this@MapsActivity, 4,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        when (result) {
            is PermissionResult.PermissionGranted -> {
                Snackbar.make(
                    mapFragment.requireView(),
                    R.string.permission_granted,
                    Snackbar.LENGTH_SHORT
                ).show()
                prepareMap(true)
            }
            else -> {
                prepareMap(false)

                Snackbar.make(
                    mapFragment.requireView(),
                    R.string.permission_denied,
                    Snackbar.LENGTH_SHORT
                )
                    .setAction(R.string.fix) {
                        AlertDialog
                            .Builder(
                                ContextThemeWrapper(
                                    this@MapsActivity,
                                    R.style.AppTheme_NoActionBar
                                )
                            )
                            .setMessage(R.string.enable_permission)
                            .setPositiveButton(R.string.yes) { _, _ ->
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivity(intent)
                            }
                            .create()
                            .show()
                    }
                    .show()
            }
        }
    }

    private fun prepareMap(hasPermission: Boolean) {
        mapFragment.getMapAsync {map ->
            googleMap = map
            if(hasPermission) {
                map.isMyLocationEnabled = true
                map.uiSettings?.isMyLocationButtonEnabled = true
                map.setOnMyLocationClickListener {
                    moveHomeMarker(LatLng(it.latitude, it.longitude))
                }
            }
            else {
                map.isMyLocationEnabled = false
                map.uiSettings.isMyLocationButtonEnabled = false
            }
            map.setOnMapClickListener {
                moveHomeMarker(it)
            }
        }
    }

    private fun moveHomeMarker(latLng: LatLng) {
        currentMarker?.remove()
        currentMarker = googleMap?.addMarker(MarkerOptions().position(latLng).title("Home position"))
    }


}
