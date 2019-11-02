package com.ghostwan.sample.geofencing.ui.maps

import android.Manifest
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.coroutinespermission.PermissionManager
import com.ghostwan.sample.geofencing.R
import com.ghostwan.sample.geofencing.utils.getGoogleMap
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.tasks.await
import org.koin.android.ext.android.inject
import kotlin.reflect.KMutableProperty0


class MapsActivity : AppCompatActivity(), MapsContract.View {


    companion object {
        const val EXTRA_LATITUDE = "EXTRA_LATITUDE"
        const val EXTRA_LONGITUDE = "EXTRA_LONGITUDE"
    }

    private val mapFragment by lazy { supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment }

    var currentHomeMarker: Pair<Marker, Circle>? = null
    var currentTmpMarker: Pair<Marker, Circle>? = null

    private val presenter by inject<MapsContract.Presenter>()
    private var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_maps, menu)
        menu.findItem(R.id.action_validate_tmp).isVisible = currentTmpMarker != null
        menu.findItem(R.id.action_clear_tmp).isVisible = currentTmpMarker != null
        menu.findItem(R.id.action_clear_current).isVisible = currentHomeMarker != null
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_validate_tmp -> {
                presenter.saveHomePosition()
                finish()
            }
            R.id.action_clear_tmp -> presenter.clearTmpPosition()
            R.id.action_clear_current -> presenter.clearSavedPosition()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onStart() {
        super.onStart()
        presenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView(this)
    }

    override suspend fun checkAndAskPermissions(): Boolean {
        val result = PermissionManager.requestPermissions(
            this@MapsActivity, 4,
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return when (result) {
            is PermissionResult.PermissionGranted -> {
                displayPermissionGranted()
                true
            }
            else -> {
                displayPermissionRefused()
                false
            }
        }
    }

    fun displayMarker(
        latLng: LatLng,
        radius: Double,
        text: Int,
        icon: Int,
        strokeColor: Int,
        fillColor: Int,
        pair: KMutableProperty0<out Pair<Marker, Circle>?>
    ) {

        val marker = googleMap?.addMarker(
            MarkerOptions()
                .position(latLng)
                .icon(BitmapDescriptorFactory.fromResource(icon))
                .title(getString(text))
        )

        val circle = googleMap?.addCircle(
            CircleOptions()
                .strokeColor(getColor(strokeColor))
                .fillColor(getColor(fillColor))
                .strokeWidth(3f)
                .center(latLng)
                .radius(radius)
        )

        if (circle != null && marker != null) {
            pair.get()?.first?.remove()
            pair.get()?.second?.remove()
            pair.setter.call(Pair(marker, circle))
            invalidateOptionsMenu()
        }

    }

    override fun displayHomeMarker(latLng: LatLng, radius: Double) {
        displayMarker(
            latLng, radius,
            R.string.current_saved_position,
            R.drawable.home,
            R.color.areaCurrentHomeStroke,
            R.color.areaCurrentHomeFill,
            ::currentHomeMarker
        )
    }

    override fun displayTmpMarker(latLng: LatLng, radius: Double) {
        displayMarker(
            latLng, radius,
            R.string.home_position_question,
            R.drawable.home_tmp,
            R.color.areaTmpHomeStroke,
            R.color.areaTmpHomeFill,
            ::currentTmpMarker
        )
    }

    fun clearPosition(pair: KMutableProperty0<out Pair<Marker, Circle>?>) {
        pair.get()?.first?.remove()
        pair.get()?.second?.remove()
        pair.setter.call(null)
        invalidateOptionsMenu()
    }

    override fun clearTmpPosition() {
        clearPosition(::currentTmpMarker)
    }

    override fun clearSavedPosition() {
        clearPosition(::currentHomeMarker)
    }

    override fun moveCamera(latLng: LatLng) {
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f))
    }


    override suspend fun getLastLocation(): Location? {
        return LocationServices.getFusedLocationProviderClient(this).lastLocation.await()
    }


    override suspend fun prepareMap(hasPermission: Boolean) {
        googleMap = mapFragment.getGoogleMap()
        if (hasPermission) {
            googleMap?.isMyLocationEnabled = true
            googleMap?.uiSettings?.isMyLocationButtonEnabled = true

        } else {
            googleMap?.isMyLocationEnabled = false
            googleMap?.uiSettings?.isMyLocationButtonEnabled = false
        }

        googleMap?.setOnMapClickListener {
            presenter.setTemporaryMarker(it)
        }
    }

    fun displayPermissionGranted() {
        Snackbar.make(
            mapFragment.requireView(),
            R.string.permission_granted,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    fun displayPermissionRefused() {
        Snackbar.make(
            mapFragment.requireView(),
            R.string.permission_denied,
            Snackbar.LENGTH_SHORT
        ).setAction(R.string.fix) {
            AlertDialog
                .Builder(ContextThemeWrapper(this@MapsActivity, R.style.AppTheme_NoActionBar))
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
