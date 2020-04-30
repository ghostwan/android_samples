package com.ghostwan.sample.notification_channels

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat

class MyReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        intent?.apply {
            val ussreId = getStringExtra("UserId")
            val notificationId = getIntExtra("notificationId",0)

            Toast.makeText(context,"Deleted ID: $ussreId",Toast.LENGTH_SHORT).show()

            context?.apply {
                // Remove the notification programmatically on button click
                NotificationManagerCompat.from(this).cancel(notificationId)
            }
        }
    }
}
