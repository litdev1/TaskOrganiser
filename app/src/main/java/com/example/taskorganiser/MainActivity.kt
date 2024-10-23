package com.example.taskorganiser

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskorganiser.actions.Action
import com.example.taskorganiser.actions.CustomAdapter
import com.example.taskorganiser.actions.StateType
import com.google.android.material.snackbar.Snackbar
import java.util.Collections

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        update()
    }

    fun setRecycler(data: ArrayList<Action>, adapter: CustomAdapter, recyclerview: RecyclerView) {
        // on below line we are creating a method to create item touch helper
        // method for adding swipe to delete functionality.
        // in this we are specifying drag direction and position to right
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                // this method is called
                // when the item is moved.
                //return false
                val from = viewHolder.adapterPosition
                val to = target.adapterPosition
                Collections.swap(data, from, to)
                adapter.notifyItemMoved(from, to)
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // this method is called when we swipe our item.
                // on below line we are getting the item at a particular position.
                val actionItem: Action =
                    data.get(viewHolder.adapterPosition)

                // below line is to get the position
                // of the item at that position.
                val position = viewHolder.adapterPosition

                if (direction == ItemTouchHelper.LEFT) {
                    // this method is called when item is swiped.
                    // below line is to remove item from our array list.
                    data.removeAt(viewHolder.adapterPosition)

                    // below line is to notify our item is removed from adapter.
                    adapter.notifyItemRemoved(viewHolder.adapterPosition)

                    // below line is to display our snackbar with action.
                    Snackbar.make(recyclerview, "Deleted " + actionItem.text, 2000)
                        .setAction(
                            "Undo",
                            View.OnClickListener {
                                // adding on click listener to our action of snack bar.
                                // below line is to add our item to array list with a position.
                                data.add(position, actionItem)

                                // below line is to notify item is
                                // added to our adapter class.
                                adapter.notifyItemInserted(position)
                            }).show()
                } else {
                    actionItem.state = StateType.YES
                    actionItem.image = R.drawable.ic_dashboard_black_24dp
                    adapter.notifyItemChanged(position)
                }
            }
            // at last we are adding this
            // to our recycler view.
        }).attachToRecyclerView(recyclerview)
    }

    fun update()
    {
        val recyclerview = findViewById<RecyclerView>(R.id.home_recycler)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)

        // This will pass the ArrayList to our Adapter
        val adapter = CustomAdapter(ApplicationClass.instance.task.children, this)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

        // that data has been updated.
        adapter.notifyDataSetChanged()

        setRecycler(ApplicationClass.instance.task.children, adapter, recyclerview)
    }
}