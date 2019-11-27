package com.ghostwan.sample.geofencing.geofencing

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

class AutoStartManager {

    private val powermanagerIntents = arrayListOf(
        intent("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"),
        intent("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"),
        intent("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"),
        intent("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity"),
        intent("com.huawei.systemmanager", "com.huawei.systemmanager.appcontrol.activity.StartupAppControlActivity"),
        intent("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"),
        intent("com.coloros.safecenter", "com.coloros.safecenter.startupapp.StartupAppListActivity"),
        intent("com.oppo.safe", "com.oppo.safe.permission.startup.StartupAppListActivity"),
        intent("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity"),
        intent("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.BgStartUpManager"),
        intent("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"),
        intent("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity"),
        intent("com.htc.pitroad", "com.htc.pitroad.landingpage.activity.LandingPageActivity"),
        intent("com.asus.mobilemanager", "com.asus.mobilemanager.MainActivity"),
        intent("com.asus.mobilemanager", "com.asus.mobilemanager.powersaver.PowerSaverSettings"),
        intent("com.evenwell.powersaving.g3", "com.evenwell.powersaving.g3.exception.PowerSaverExceptionActivity")
    )

    fun isSettingsHandle(context: Context): Boolean {
        for (powermanagerIntent in powermanagerIntents) {
            if (context.packageManager.resolveActivity(powermanagerIntent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                return true
            }
        }
        return false
    }

    fun startSettings(context: Context) {
        for (powermanagerIntent in powermanagerIntents) {
            if (context.packageManager.resolveActivity(powermanagerIntent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                context.startActivity(powermanagerIntent)
                break
            }
        }
    }

    fun intent(pkg: String, clazz: String): Intent {
        return Intent().setComponent(ComponentName(pkg, clazz))
    }
}