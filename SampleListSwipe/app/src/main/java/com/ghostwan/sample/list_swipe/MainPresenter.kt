package com.ghostwan.sample.list_swipe

class MainPresenter : Contract.Presenter {

    private var view: Contract.View? = null
    private lateinit var items: MutableList<Item>

    override fun attach(view: Contract.View) {
        this.view = view
    }

    override fun detatch(view: Contract.View) {
        if(view == this.view) {
            this.view = null
        }
    }

    override fun loadItems() {
        val data = arrayOf("apple", "banana", "chocolate")
        items = data.map { Item(it) }.toMutableList()
        view?.displayItems(items)
    }

    override fun remove(index: Int) {
        items.removeAt(index)
        view?.removeItem(index)
    }

    override fun add(index: Item) {
        items.add(index)
        view?.displayItems(items)
    }

    override fun postpone(position: Int) {
        view?.displayItems(items)
    }

}