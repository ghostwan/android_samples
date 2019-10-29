package com.ghostwan.sample.geofencing.ui

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.coroutinespermission.PermissionManager
import com.ghostwan.sample.geofencing.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class MapsActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_LATITUDE = "EXTRA_LATITUDE"
        const val EXTRA_LONGITUDE = "EXTRA_LONGITUDE"
    }
    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    private val mapFragment by lazy { supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        //FIXME can't use the API because no credit card attached yet
        //enableSearchingPlaces()
    }

    override fun onStart() {
        super.onStart()
        getPermissions()
    }

    private fun getPermissions() {
        uiScope.launch {
            val result = PermissionManager.requestPermissions(
                this@MapsActivity, 4,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            Log.d("debug", "Result : $result")
            when(result) {
                is PermissionResult.PermissionGranted -> {
                    Snackbar.make(mapFragment.requireView(), R.string.permission_granted, Snackbar.LENGTH_SHORT).show()
                    mapFragment.getMapAsync {
                        it.isMyLocationEnabled = true
                        it.uiSettings.isMyLocationButtonEnabled = true
                        onMapReady(it)
                    }
                }
                else -> {
                    mapFragment.getMapAsync {
                        it.isMyLocationEnabled = false
                        it.uiSettings.isMyLocationButtonEnabled = false
                        onMapReady(it)
                    }

                    Snackbar.make(mapFragment.requireView(), R.string.permission_denied, Snackbar.LENGTH_SHORT)
                        .setAction(R.string.fix) {
                            AlertDialog
                                .Builder(ContextThemeWrapper(this@MapsActivity, R.style.AppTheme_NoActionBar))
                                .setMessage(R.string.enable_permission)
                                .setPositiveButton(R.string.yes) { dialog, id ->
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
    }

    private fun onMapReady(map: GoogleMap) {
        map.setOnMapClickListener {
            title = "${it.latitude} ${it.longitude}"
        }
        map.setOnPoiClickListener {poi ->
            Toast.makeText(
                applicationContext, "Clicked: " +
                    poi.name + "\nPlace ID:" + poi.placeId +
                    "\nLatitude:" + poi.latLng.latitude +
                    " Longitude:" + poi.latLng.longitude,
                Toast.LENGTH_SHORT).show();
        }
    }

    //FIXME uncomment and uncomment in layout when enabling places
//    private fun enableSearchingPlaces() {
//        // Initialize Places.
//        if (!Places.isInitialized()) {
//            Places.initialize(this, getString(R.string.GOOGLE_MAP_KEY))
//        }
//        val placesClient = Places.createClient(this)
//
//        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
//        // and once again when the user makes a selection (for example when calling fetchPlace()).
//        val token = AutocompleteSessionToken.newInstance()
//
//        // Specify the fields to return.
//        val placeFields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
//
//        val autocompleteFragment = supportFragmentManager.findFragmentById(R.id.place_autocomplete_fragment) as AutocompleteSupportFragment
//        autocompleteFragment.setPlaceFields(placeFields)
//    }

}
