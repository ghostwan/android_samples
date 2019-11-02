package com.ghostwan.sample.geofencing.ui.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.ContextThemeWrapper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ghostwan.sample.geofencing.R
import com.ghostwan.sample.geofencing.data.Source
import com.ghostwan.sample.geofencing.data.model.Event
import com.ghostwan.sample.geofencing.ui.EventAdapter
import com.ghostwan.sample.geofencing.ui.maps.MapsActivity
import kotlinx.android.synthetic.main.activity_scrolling.*
import kotlinx.android.synthetic.main.content_scrolling.*
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity(), MainContract.View {


    companion object {
        const val INTENT_UPDATE_STATUS: String = "INTENT_UPDATE_STATUS"
        const val MAPS_REQUEST_CODE = 1
    }

    val presenter by inject<MainContract.Presenter>()

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            presenter.updateStatus()
        }
    }

    private val viewManager: RecyclerView.LayoutManager by lazy { LinearLayoutManager(this) }
    private val viewAdapter: EventAdapter by lazy { EventAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scrolling)
        setSupportActionBar(toolbar)
        title = ""

        viewAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                viewManager.smoothScrollToPosition(eventList, null, 0)
            }
        })

        eventList.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }


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
        presenter.refreshEventList()
    }

    override fun showEventList(events: List<Event>) {
        viewAdapter.submitList(events.sortedByDescending { it.date })
    }

    override fun askIsHome() {
        AlertDialog
            .Builder(
                ContextThemeWrapper(
                    this,
                    R.style.AppTheme_NoActionBar
                )
            )
            .setMessage(R.string.are_you_home)
            .setPositiveButton(R.string.yes) { dialog, id ->
                presenter.enterHome(Source.App)
                openHomeLocationActivity()
            }
            .setNegativeButton(R.string.no) { dialog, id ->
                presenter.leaveHome(Source.App)
            }
            .create()
            .show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_clear_tmp -> presenter.clearDatabase()
            R.id.action_set_home_location -> openHomeLocationActivity()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun openHomeLocationActivity() {
        startActivityForResult(Intent(this, MapsActivity::class.java), MAPS_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            MAPS_REQUEST_CODE -> data?.let {
                presenter.setHomeLocation(
                    data.getLongExtra(MapsActivity.EXTRA_LATITUDE, 0),
                    data.getLongExtra(MapsActivity.EXTRA_LONGITUDE, 0)
                )
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
