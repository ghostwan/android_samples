package com.ghostwan.sample.geofencing.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

abstract class BaseFragment : Fragment(), CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext = job + Dispatchers.Main

    companion object {
        const val INTENT_UPDATE_STATUS: String = "INTENT_UPDATE_STATUS"
    }

    private val broadcastManager by lazy { LocalBroadcastManager.getInstance(context!!) }

    private val broadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            getPresenter().checkStateMachine()
        }
    }

    abstract fun getPresenter(): BaseContract.BasePresenter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        broadcastManager.registerReceiver(broadcastReceiver, IntentFilter(INTENT_UPDATE_STATUS))
    }

    override fun onDetach() {
        super.onDetach()
        broadcastManager.unregisterReceiver(broadcastReceiver)
    }
}