package com.ghostwan.sample.geofencing.geofencing

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.ghostwan.sample.geofencing.R
import com.ghostwan.sample.geofencing.utils.elseNull
import com.ghostwan.sample.geofencing.utils.ifNotNull

class NotificationManager(val context: Context) {

    private val notificationManager: NotificationManager by lazy { context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }
    private val geoFencingChannel: NotificationChannel by lazy {
        createChannel("Detect Geofencing", NotificationManager.IMPORTANCE_HIGH)
        notificationManager.notificationChannels[0]
    }

    fun display(title: String, message: String, errorMessage: String? = null) {

        val displayMessage = errorMessage?.ifNotNull {
            "$message: +$it"
        } ?: elseNull {
            message
        }
        val builder = NotificationCompat.Builder(context, geoFencingChannel.id)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(displayMessage)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            notify(0, builder.build())
        }
    }

    fun display(resTitle: Int, resMessage: Int, errorMessage: String? = null) {
        display(context.getString(resTitle), context.getString(resMessage), errorMessage)
    }


    fun createChannel(channelName: String, importance: Int) {
        val id = "Channel ID $channelName"
        val channel = NotificationChannel(id, channelName, importance).apply {
            description = when (importance) {
                NotificationManager.IMPORTANCE_HIGH -> "Shows everywhere, makes noise and peeks. May use full screen."
                NotificationManager.IMPORTANCE_DEFAULT -> "Shows everywhere, makes noise, but does not visually intrude."
                NotificationManager.IMPORTANCE_LOW -> "Shows everywhere, but is not intrusive."
                else -> "Other importance"
            }
        }
        notificationManager.createNotificationChannel(channel)
    }
}