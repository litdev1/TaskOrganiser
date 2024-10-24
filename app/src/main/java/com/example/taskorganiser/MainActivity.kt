package com.example.taskorganiser

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskorganiser.actions.Action
import com.example.taskorganiser.actions.ActionType
import com.example.taskorganiser.actions.CustomAdapter
import com.example.taskorganiser.actions.StateType
import com.google.android.material.snackbar.Snackbar
import java.util.Collections

class MainActivity : AppCompatActivity() {
    var itemTouchHelper: ItemTouchHelper? = null;

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

        findViewById<Button>(R.id.buttonHome).setOnClickListener { view ->
            ApplicationClass.instance.task = ApplicationClass.instance.data
            ApplicationClass.instance.task.reset()
            update()
        }

        findViewById<Button>(R.id.buttonBack).setOnClickListener { view ->
            if (null != ApplicationClass.instance.task.parent) {
                ApplicationClass.instance.task = ApplicationClass.instance.task.parent!!
                update()
            }
        }

        findViewById<Button>(R.id.buttonEdit).setOnClickListener { view ->
            val intent = Intent(this, EditActivity::class.java)
            startActivity(intent);
        }
    }

    fun update()
    {
        val recyclerView = findViewById<RecyclerView>(R.id.home_recycler)

        // this creates a vertical layout Manager
        recyclerView.layoutManager = LinearLayoutManager(this)

        // This will pass the ArrayList to our Adapter
        title = ApplicationClass.instance.task.text
        val adapter = CustomAdapter(ApplicationClass.instance.task.children, this, recyclerView)

        // Setting the Adapter with the recyclerview
        recyclerView.adapter = adapter

        if (itemTouchHelper != null)
        {
            itemTouchHelper?.attachToRecyclerView(null)
            itemTouchHelper = null
        }
        itemTouchHelper = adapter.setTouchHelper(adapter, recyclerView)
        itemTouchHelper?.attachToRecyclerView(recyclerView)

        // that data has been updated.
        adapter.notifyDataSetChanged()
    }
}