package com.ghostwan.sample.geofencing.ui.event

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ghostwan.sample.geofencing.R
import com.ghostwan.sample.geofencing.data.model.Event
import kotlinx.android.synthetic.main.event_row.view.*

class EventAdapter : ListAdapter<Event, EventAdapter.Holder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.event_row, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position))
    }

    class Holder(row: View) : RecyclerView.ViewHolder(row) {
        fun bind(event: Event) {
            itemView.event_date.text = event.date.toString()
            itemView.event_state.setText(event.isHome.let {
                if (it) R.string.i_am_home else R.string.i_left_home
            })
            itemView.event_source.text = event.source.name
            itemView.setOnClickListener { }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Event>() {
        override fun areItemsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Event, newItem: Event): Boolean {
            return oldItem == newItem
        }
    }
}