package com.ghostwan.sample.notification_channels

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private var notificationCpt = 0

    private val notificationManager: NotificationManager by lazy { getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createGroupButton.setOnClickListener {
            createChannelGroup("Home 1")
            createChannelGroup("Home 2")
            checkButtonStatus()
        }
        removeGroupButton.setOnClickListener {
            notificationManager.notificationChannelGroups.forEach {
                notificationManager.deleteNotificationChannelGroup(it.id)
            }
            checkButtonStatus()
        }
        openSettingsButton.setOnClickListener {  openNotificationSetiings()}
        generateButton.setOnClickListener { generateNotifications() }
        checkButtonStatus()
    }

    private fun generateNotifications() {
        val channels = notificationManager.notificationChannels
        for (channel in channels) {
            createNotification(channel)
        }
    }

    private fun openNotificationSetiings() {
        val settingsIntent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        startActivity(settingsIntent)
    }

    private fun createChannelGroup(name: String) {
        val groupID = createGroup(name)
        createChannel(groupID, "Super important notification", NotificationManager.IMPORTANCE_HIGH)
        createChannel(groupID, "Normal notification", NotificationManager.IMPORTANCE_DEFAULT)
        createChannel(groupID, "Not important notification", NotificationManager.IMPORTANCE_LOW)
    }

    fun createGroup(name: String): String {
        val groupId = "Group ID $name"
        notificationManager.createNotificationChannelGroup(NotificationChannelGroup(groupId, name))
        return groupId
    }

    fun createChannel(groupID: String, channelName: String, importance: Int){
        val id = "Channel ID $channelName - $groupID"
        val channel = NotificationChannel(id, channelName, importance).apply {
            description = when (importance) {
                NotificationManager.IMPORTANCE_HIGH -> "Shows everywhere, makes noise and peeks. May use full screen."
                NotificationManager.IMPORTANCE_DEFAULT-> "Shows everywhere, makes noise, but does not visually intrude."
                NotificationManager.IMPORTANCE_LOW-> "Shows everywhere, but is not intrusive."
                else -> "Other importance"
            }
            group = groupID
        }
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(channel: NotificationChannel) {
        val builder = NotificationCompat.Builder(this, channel.id)
            .setSmallIcon(R.drawable.notif_asset)
            .setContentTitle("Test ${channel.name} for ${channel.group}")
            .setContentText("Content of ${channel.name}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(notificationCpt++, builder.build())
        }
    }

    fun isChannelsExist(): Boolean {
        return notificationManager.notificationChannelGroups.isNotEmpty()
    }

    fun checkButtonStatus() {
        if(isChannelsExist()) {
            createGroupButton.visibility = View.INVISIBLE
            removeGroupButton.visibility = View.VISIBLE
        }
        else {
            createGroupButton.visibility = View.VISIBLE
            removeGroupButton.visibility = View.INVISIBLE
        }
        generateButton.visibility = removeGroupButton.visibility
    }

}

