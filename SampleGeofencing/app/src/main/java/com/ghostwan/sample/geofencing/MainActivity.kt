package com.ghostwan.sample.geofencing

import android.Manifest
import android.Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.ghostwan.sample.geofencing.geofencing.GeofencingManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import org.koin.android.ext.android.inject
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {

    private val geofencingManager by inject<GeofencingManager>()
    private val job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.Main

    private val powerManagerIntents = arrayOf(
        Intent().setComponent(ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")),
        Intent().setComponent(ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity")),
        Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity")),
        Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")),
        Intent().setComponent(ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity")),
        Intent().setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity")),
        Intent().setComponent(ComponentName("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity")),
        Intent().setComponent(ComponentName("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity")),
        Intent().setComponent(ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity")),
        Intent().setComponent(ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager")),
        Intent().setComponent(ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity")),
        Intent().setComponent(ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")),
        Intent().setComponent(ComponentName("com.htc.pitroad", "com.htc.pitroad.landingpage.activity.LandingPageActivity")),
        Intent().setComponent(ComponentName("com.asus.mobilemanager", "com.asus.mobilemanager.MainActivity"))
    )

    private val permissionCompat by lazy {
        LocationPermissionCompat(this, this, listener)
    }
    private val listener = object : LocationPermissionCompat.Listener {
        override fun onPermissionsAccepted(isLocationServiceEnabled: Boolean, isAppOptimized: Boolean) {
            when {
                !isLocationServiceEnabled -> toast(R.string.enable_location)
                !isAppOptimized -> askForUnoptimized()
                else -> geofencingManager.registerGeofencing(true)
            }
        }

        override fun onPermissionsRefused() {
            Toast.makeText(this@MainActivity, "If you don't grant all the required permission the app won't work correctly", Toast.LENGTH_LONG).show()
        }

    }

    private fun toast(resource: Int) {
        Toast.makeText(this@MainActivity, resource, Toast.LENGTH_LONG).show()
    }

    private val startOptimizedActivity =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            permissionCompat.checkAndAskPermission()
        }

    fun askForUnoptimized(){
        AlertDialog.Builder(this)
            .setTitle(R.string.app_optimized_title)
            .setMessage(R.string.app_optimized_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                val systemIntent = Intent()
                systemIntent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                systemIntent.data = Uri.parse("package:${packageName}")
                val powerIntents = arrayListOf(systemIntent)
                powerIntents.addAll(powerManagerIntents)
                for (intent in powerIntents) {
                    val resolveInfo = intent.resolveActivityInfo(packageManager, PackageManager.MATCH_DEFAULT_ONLY)
                    if (resolveInfo?.exported == true) {
                        startOptimizedActivity.launch(intent)
                        break
                    }
                }
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                listener.onPermissionsRefused()
                dialog.dismiss()
            }
            .create()
            .show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_event, R.id.navigation_map)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        permissionCompat.checkAndAskPermission()
    }

}
