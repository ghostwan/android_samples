package com.ghostwan.sample.list_swipe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_row.view.*

class ItemAdapter(private val itemListener: (Item) -> Unit) : ListAdapter<Item, ItemAdapter.Holder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_row, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(getItem(position), itemListener)
    }

    class Holder(row: View): RecyclerView.ViewHolder(row) {
        fun bind(item: Item, clickListener: (Item) -> Unit) {
            itemView.itemTitle.text = item.value
            itemView.setOnClickListener { clickListener(item) }
        }
    }
}