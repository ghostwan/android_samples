package com.ghostwan.sample.list_swipe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import androidx.recyclerview.widget.ItemTouchHelper


class MainActivity : AppCompatActivity(), Contract.View {

    private val presenter: Contract.Presenter by lazy { MainPresenter() }
    private val itemAdapter: ItemAdapter by lazy { ItemAdapter(this::displayItem) }
    private val viewManager: RecyclerView.LayoutManager by lazy { LinearLayoutManager(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        itemList.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = itemAdapter
        }
        val deleteTouchListener = ItemTouchHelper(SwipeToDeleteCallback(this, presenter))
        deleteTouchListener.attachToRecyclerView(itemList)
        val postponeTouchListener = ItemTouchHelper(SwipeToPostponeCallback(this, presenter))
        postponeTouchListener.attachToRecyclerView(itemList)
    }

    override fun onStart() {
        super.onStart()
        presenter.attach(this)
        presenter.loadItems()
    }

    override fun onStop() {
        super.onStop()
        presenter.detatch(this)
    }

    override fun displayItems(items: List<Item>) {
        itemAdapter.submitList(items)
        itemAdapter.notifyDataSetChanged()
    }

    fun displayItem(item: Item) {
        Snackbar.make(itemList, "Item : ${item.value}", Snackbar.LENGTH_SHORT).show()
    }

    override fun removeItem(index: Int) {
        itemAdapter.notifyItemRemoved(index)
    }

    override fun changeItem(index: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
