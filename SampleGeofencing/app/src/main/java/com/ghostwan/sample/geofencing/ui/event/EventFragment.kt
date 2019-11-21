package com.ghostwan.sample.geofencing.ui.event

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ghostwan.sample.geofencing.R
import com.ghostwan.sample.geofencing.data.Source
import com.ghostwan.sample.geofencing.data.model.Event
import com.ghostwan.sample.geofencing.ui.BaseContract
import com.ghostwan.sample.geofencing.ui.BaseFragment
import com.ghostwan.sample.geofencing.ui.maps.MapFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.koin.android.ext.android.inject


class EventFragment : BaseFragment(), EventContract.View {

    companion object {
        const val MAPS_REQUEST_CODE = 1
    }

    private val presenter by inject<EventContract.Presenter>()

    private val viewManager: RecyclerView.LayoutManager by lazy { LinearLayoutManager(context) }
    private val viewAdapter: EventAdapter by lazy { EventAdapter() }
    private var root: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun getPresenter(): BaseContract.BasePresenter {
        return presenter
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.fragment_event, container, false)
        val eventList = root?.findViewById<RecyclerView>(R.id.eventList)

        viewAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                viewManager.smoothScrollToPosition(eventList, null, 0)
            }
        })

        root?.findViewById<RecyclerView>(R.id.eventList)?.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_event, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_clear_tmp -> presenter.clearDatabase()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter.attachView(this)
    }

    override fun onDetach() {
        super.onDetach()
        presenter.detachView(this)
    }

    override fun setIsHome(isHome: Boolean) {
        val fab = root?.findViewById<FloatingActionButton>(R.id.fab)
        if (isHome) {
            // If I am home I want to say that I left
            activity?.title = getString(R.string.i_am_home)
            fab?.setImageResource(R.drawable.exit_home)
            fab?.backgroundTintList = ColorStateList.valueOf(context!!.getColor(R.color.leftTint))
            fab?.setOnClickListener { presenter.leaveHome(Source.App) }
        } else {
            // If I am not home I want to say that I came
            activity?.title = getString(R.string.i_left_home)
            fab?.setImageResource(R.drawable.enter_home)
            fab?.backgroundTintList = ColorStateList.valueOf(context!!.getColor(R.color.homeTint))
            fab?.setOnClickListener { presenter.enterHome(Source.App) }
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
                    context,
                    R.style.AppTheme_NoActionBar
                )
            )
            .setMessage(R.string.are_you_home)
            .setPositiveButton(R.string.yes) { dialog, id ->
                presenter.enterHome(Source.App)
                findNavController().navigate(R.id.navigation_map)
            }
            .setNegativeButton(R.string.no) { dialog, id ->
                presenter.leaveHome(Source.App)
            }
            .create()
            .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            MAPS_REQUEST_CODE -> data?.let {
                presenter.setHomeLocation(
                    data.getLongExtra(MapFragment.EXTRA_LATITUDE, 0),
                    data.getLongExtra(MapFragment.EXTRA_LONGITUDE, 0)
                )
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
