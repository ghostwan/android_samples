package com.ghostwan.sample.geofencing.ui.maps

import android.Manifest
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.coroutinespermission.PermissionManager
import com.ghostwan.sample.geofencing.MainApplication
import com.ghostwan.sample.geofencing.R
import com.ghostwan.sample.geofencing.ui.BaseContract
import com.ghostwan.sample.geofencing.ui.BaseFragment
import com.ghostwan.sample.geofencing.utils.getGoogleMap
import com.ghostwan.sample.geofencing.utils.ifNotNull
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KMutableProperty0


class MapFragment : BaseFragment(), MapContract.View, CoroutineScope {


    companion object {
        const val EXTRA_LATITUDE = "EXTRA_LATITUDE"
        const val EXTRA_LONGITUDE = "EXTRA_LONGITUDE"
    }

    private val mapFragment by lazy { childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment }

    private val job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.Main

    var currentHomeMarker: Pair<Marker, Circle>? = null
    var currentTmpMarker: Pair<Marker, Circle>? = null

    private val presenter by inject<MapContract.Presenter>()
    private var googleMap: GoogleMap? = null
    private lateinit var root: View

    override fun getPresenter(): BaseContract.BasePresenter {
        return presenter
    }

    override fun onCreate(p0: Bundle?) {
        super.onCreate(p0)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        root = inflater.inflate(R.layout.fragment_map, container, false)
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_map, menu)
        menu.findItem(R.id.action_validate_tmp).isVisible = currentTmpMarker != null
        menu.findItem(R.id.action_clear_tmp).isVisible = currentTmpMarker != null
        menu.findItem(R.id.action_clear_current).isVisible = currentHomeMarker != null

        launch {
            activity?.title = when {
                currentHomeMarker == null -> getString(R.string.select_home_location)
                presenter.isHome() -> getString(R.string.i_am_home)
                else -> getString(R.string.i_left_home)
            }
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_validate_tmp -> {
                presenter.saveHomePosition()
            }
            R.id.action_clear_tmp -> presenter.clearTmpPosition()
            R.id.action_clear_current -> presenter.clearSavedPosition()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter.attachView(this)
    }

    override fun onDetach() {
        super.onDetach()
        presenter.detachView(this)
    }

    override suspend fun checkAndAskPermissions(): Boolean {
        val result = PermissionManager.requestPermissions(
            this@MapFragment, 4,
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
        fillColor: Int,
        strokeColor: Int,
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
                .fillColor(context!!.getColor(fillColor))
                .strokeColor(context!!.getColor(strokeColor))
                .strokeWidth(3f)
                .center(latLng)
                .radius(radius)
        )

        if (circle != null && marker != null) {
            pair.get()?.first?.remove()
            pair.get()?.second?.remove()
            pair.setter.call(Pair(marker, circle))
            updateActionBar()
        }

    }

    override fun displayHomeMarker(latLng: LatLng, radius: Double) {
        launch {
            displayMarker(
                latLng, radius,
                R.string.current_saved_position,
                R.drawable.home,
                if (presenter.isHome()) R.color.homePrimary else R.color.leftPrimary,
                if (presenter.isHome()) R.color.homePrimaryDark else R.color.leftPrimaryDark,
                ::currentHomeMarker
            )
        }

    }

    override fun displayTmpMarker(latLng: LatLng, radius: Double) {
        displayMarker(
            latLng, radius,
            R.string.home_position_question,
            R.drawable.home_tmp,
            R.color.tmpPrimary,
            R.color.tmpPrimaryDark,
            ::currentTmpMarker
        )
    }

    fun clearPosition(pair: KMutableProperty0<out Pair<Marker, Circle>?>) {
        pair.get()?.first?.remove()
        pair.get()?.second?.remove()
        pair.setter.call(null)
        updateActionBar()
    }

    override fun clearTmpPosition() {
        clearPosition(::currentTmpMarker)
    }

    override fun clearHomePosition() {
        clearPosition(::currentHomeMarker)
    }

    override fun moveCamera(latLng: LatLng) {
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f))
    }


    override suspend fun getLastLocation(): Location? {
        context?.ifNotNull {
            return LocationServices.getFusedLocationProviderClient(it).lastLocation.await()
        }
        return null
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
        Log.i(MainApplication.TAG, "location permission granted")
    }

    fun displayPermissionRefused() {
        Snackbar.make(
            root,
            R.string.permission_denied,
            Snackbar.LENGTH_SHORT
        ).setAction(R.string.fix) {
            AlertDialog
                .Builder(ContextThemeWrapper(context, R.style.AppTheme_NoActionBar))
                .setMessage(R.string.enable_permission)
                .setPositiveButton(R.string.yes) { _, _ ->
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    val uri = Uri.fromParts("package", activity?.packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
                .create()
                .show()
        }
            .show()
    }

    fun updateActionBar() {
        activity?.invalidateOptionsMenu()
    }

}
