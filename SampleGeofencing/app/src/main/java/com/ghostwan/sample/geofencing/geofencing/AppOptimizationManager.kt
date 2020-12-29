package com.ghostwan.sample.geofencing.geofencing

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCaller
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import com.ghostwan.sample.geofencing.R

class AppOptimizationManager(
    private val context: Context) {

    private lateinit var startOptimizedActivity : ActivityResultLauncher<Intent>
    private var listener: ResultListener?=null

    fun init(caller: ActivityResultCaller) {
        startOptimizedActivity = caller.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(checkApplicationUnoptimized()) {
                listener?.onAppEfficient()
            } else {
                listener?.onAppOptimized()
            }
        }
    }

    interface ResultListener {
        fun onAppEfficient()
        fun onAppOptimized()
    }

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

    fun checkOrAskUnoptimization(builder: AlertDialog.Builder,
                                 listener: ResultListener) {
        this.listener = listener

        if(checkApplicationUnoptimized()) {
            listener.onAppEfficient()
        } else {
            builder
                .setTitle(R.string.app_optimized_title)
                .setMessage(R.string.app_optimized_message)
                .setPositiveButton(R.string.yes) { _, _ ->
                    val systemIntent = Intent()
                    systemIntent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    systemIntent.data = Uri.parse("package:${context.packageName}")
                    val powerIntents = arrayListOf(systemIntent)
                    powerIntents.addAll(powerManagerIntents)
                    for (intent in powerIntents) {
                        val resolveInfo = intent.resolveActivityInfo(context.packageManager, PackageManager.MATCH_DEFAULT_ONLY)
                        if (resolveInfo?.exported == true) {
                            startOptimizedActivity.launch(intent)
                            break
                        }
                    }
                }
                .setNegativeButton(R.string.no) { dialog, _ ->
                    listener.onAppOptimized()
                    dialog.dismiss()
                }
                .create()
                .show()
        }
    }

    fun checkApplicationUnoptimized(): Boolean {
        // Since Android 6 (API 23) Doze it optimizing background work which could affect geofencing
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            (context.getSystemService(Context.POWER_SERVICE) as PowerManager)
                .isIgnoringBatteryOptimizations(context.packageName)
        } else {
            false
        }
    }



}