package com.example.taskorganiser

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskorganiser.actions.CustomAdapter
import android.widget.Toast

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

        findViewById<Button>(R.id.buttonMainHome).setOnClickListener { view ->
            ApplicationClass.instance.data.reset()
            ApplicationClass.instance.data.setParents(null)
            ApplicationClass.instance.task = ApplicationClass.instance.data
            update()
        }

        findViewById<Button>(R.id.buttonMainBack).setOnClickListener { view ->
            if (null != ApplicationClass.instance.task.parent) {
                ApplicationClass.instance.task = ApplicationClass.instance.task.parent!!
                update()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.editTasks -> {
                ApplicationClass.instance.data.reset()
                ApplicationClass.instance.data.setParents(null)
                val intent = Intent(this, EditActivity::class.java)
                startActivity(intent);
            }
            R.id.settings -> Toast.makeText(this,"Settings Selected",Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    fun update()
    {
        val recyclerView = findViewById<RecyclerView>(R.id.main_recycler)

        // this creates a vertical layout Manager
        recyclerView.layoutManager = LinearLayoutManager(this)

        // This will pass the ArrayList to our Adapter
        title = ApplicationClass.instance.task.text
        val adapter = CustomAdapter(ApplicationClass.instance.task.children, ::update, recyclerView, false)

        // Setting the Adapter with the recyclerview
        recyclerView.adapter = adapter

        if (itemTouchHelper != null) {
            itemTouchHelper?.attachToRecyclerView(null)
            itemTouchHelper = null
        }
        itemTouchHelper = adapter.setTouchHelper(adapter, recyclerView, adapter.editable)
        itemTouchHelper?.attachToRecyclerView(recyclerView)

        // that data has been updated.
        adapter.notifyDataSetChanged()
    }
}