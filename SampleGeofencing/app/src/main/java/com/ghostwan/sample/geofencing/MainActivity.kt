package com.ghostwan.sample.geofencing

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import kotlinx.android.synthetic.main.activity_scrolling.*
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity(), MainContract.View {

    companion object {
        const val INTENT_UPDATE_STATUS: String = "INTENT_UPDATE_STATUS"
    }

    val presenter by inject<MainContract.Presenter>()

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            presenter.updateStatus()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        title = ""

    }

    override fun onResume() {
        super.onResume()
        registerReceiver(broadcastReceiver, IntentFilter(INTENT_UPDATE_STATUS))
        presenter.attachView(this)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(broadcastReceiver)
        presenter.detachView(this)
    }

    override fun setIsHome(isHome: Boolean) {
        if (isHome) {
            // If I am home I want to say that I left
            runOnUiThread {
                toolbar_layout?.title = getString(R.string.i_am_home)
                fab.setImageResource(R.drawable.exit_home)
                fab.setOnClickListener { presenter.leaveHome(Source.App) }
            }
        } else {
            // If I am not home I want to say that I came
            runOnUiThread {
                toolbar_layout?.title = getString(R.string.i_left_home)
                fab.setImageResource(R.drawable.enter_home)
                fab.setOnClickListener { presenter.enterHome(Source.App) }
            }
        }
    }

    override fun askIsHome() {
        AlertDialog
            .Builder(ContextThemeWrapper(this, R.style.AppTheme_NoActionBar))
            .setMessage(R.string.are_you_home)
            .setPositiveButton(R.string.yes) { dialog, id ->
                presenter.enterHome(Source.App)
            }
            .setNegativeButton(R.string.no) { dialog, id ->
                presenter.leaveHome(Source.App)
            }
            .create()
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_scrolling, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
