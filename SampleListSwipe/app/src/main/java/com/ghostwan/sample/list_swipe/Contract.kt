package com.ghostwan.sample.list_swipe

interface Contract {
    interface Presenter {
        fun attach(view: View)
        fun detatch(view: View)
        fun loadItems()
        fun remove(item: Int)
        fun add (item: Item)
        fun postpone(position: Int)
    }

    interface View {
        fun displayItems(items: List<Item>)
        fun removeItem(index: Int)
        fun changeItem(index: Int)
    }

}