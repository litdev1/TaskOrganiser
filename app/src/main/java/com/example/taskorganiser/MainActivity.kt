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

    fun update()
    {
        val recyclerview = findViewById<RecyclerView>(R.id.home_recycler)

        // this creates a vertical layout Manager
        recyclerview.layoutManager = LinearLayoutManager(this)

        // This will pass the ArrayList to our Adapter
        val actions = ApplicationClass.instance.task.children
        val adapter = CustomAdapter(actions, this)

        // Setting the Adapter with the recyclerview
        recyclerview.adapter = adapter

        // that data has been updated.
        adapter.notifyDataSetChanged()

        adapter.setRecycler(actions, adapter, recyclerview)
    }
}