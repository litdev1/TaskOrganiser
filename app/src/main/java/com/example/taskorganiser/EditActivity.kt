package com.example.taskorganiser

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.taskorganiser.actions.CustomAdapter

class EditActivity : AppCompatActivity() {
    var itemTouchHelper: ItemTouchHelper? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.edit)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        update()

        findViewById<Button>(R.id.buttonEditHome).setOnClickListener { view ->
            ApplicationClass.instance.data.reset()
            ApplicationClass.instance.data.setParents(null)
            ApplicationClass.instance.task = ApplicationClass.instance.data
            update()
        }

        findViewById<Button>(R.id.buttonEditBack).setOnClickListener { view ->
            if (null != ApplicationClass.instance.task.parent) {
                ApplicationClass.instance.task = ApplicationClass.instance.task.parent!!
                update()
            }
        }

        findViewById<Button>(R.id.buttonEndEdit).setOnClickListener { view ->
            val recyclerView = findViewById<RecyclerView>(R.id.edit_recycler)
            for (i in 0..recyclerView.childCount - 1)
            {
                val holder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i)) as CustomAdapter.ViewHolder
                ApplicationClass.instance.task.children[holder.layoutPosition].text = holder.textEditView.text.toString()
            }
            ApplicationClass.instance.data.save(cacheDir.toString())
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent);
        }
    }

    fun update()
    {
        val recyclerView = findViewById<RecyclerView>(R.id.edit_recycler)

        // this creates a vertical layout Manager
        recyclerView.layoutManager = LinearLayoutManager(this)

        // This will pass the ArrayList to our Adapter
        title = ApplicationClass.instance.task.text
        val adapter = CustomAdapter(ApplicationClass.instance.task.children, ::update, recyclerView, true)

        // Setting the Adapter with the recyclerview
        recyclerView.adapter = adapter

        if (adapter.editable) {
            if (itemTouchHelper != null) {
                itemTouchHelper?.attachToRecyclerView(null)
                itemTouchHelper = null
            }
            itemTouchHelper = adapter.setTouchHelper(adapter, recyclerView)
            itemTouchHelper?.attachToRecyclerView(recyclerView)
        }

        // that data has been updated.
        adapter.notifyDataSetChanged()
    }
}