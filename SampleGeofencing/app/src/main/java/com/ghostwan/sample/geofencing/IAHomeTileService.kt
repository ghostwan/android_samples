package com.ghostwan.sample.geofencing

import android.content.Intent
import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import com.ghostwan.sample.geofencing.data.Source
import com.ghostwan.sample.geofencing.data.model.Event
import com.ghostwan.sample.geofencing.ui.MainActivity
import com.ghostwan.sample.geofencing.ui.MainContract
import org.koin.android.ext.android.inject

class IAHomeTileService : TileService(), MainContract.View {


    private val presenter by inject<MainContract.Presenter>()

    override fun onStartListening() {
        super.onStartListening()
        presenter.attachView(this)
    }

    override fun onStopListening() {
        super.onStopListening()
        presenter.detachView(this)
    }
    override fun onClick() {
        super.onClick()
        if (qsTile.label == getString(R.string.leave_home)) {
            presenter.leaveHome(Source.Tile)
        } else {
            presenter.enterHome(Source.Tile)
        }
    }

    override fun setIsHome(isHome: Boolean) {
        if (isHome) {
            // If I am home I want to say that I left
            qsTile.icon = Icon.createWithResource(this, R.drawable.exit_home)
            qsTile.label = getString(R.string.leave_home)
            qsTile.contentDescription = getString(R.string.i_am_home)
            qsTile.state = Tile.STATE_ACTIVE
        } else {
            // If I am not home I want to say that I came
            qsTile.icon = Icon.createWithResource(this, R.drawable.enter_home)
            qsTile.label = getString(R.string.come_home)
            qsTile.contentDescription = getString(R.string.i_left_home)
            qsTile.state = Tile.STATE_INACTIVE
        }
        qsTile.updateTile()
        sendBroadcast(Intent(MainActivity.INTENT_UPDATE_STATUS))
    }

    override fun askIsHome() {
        startActivity(Intent(this, MainActivity::class.java).also { it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
    }

    override fun showEventList(events: List<Event>) {
    }

}
