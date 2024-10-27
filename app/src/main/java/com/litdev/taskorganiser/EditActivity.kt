package com.litdev.taskorganiser

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.litdev.taskorganiser.actions.ActionType
import com.litdev.taskorganiser.actions.CustomAdapter
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView

class EditActivity : AppCompatActivity() {
    var itemTouchHelper: ItemTouchHelper? = null
    lateinit var adapter: CustomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.edit)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        (findViewById(R.id.toolBarImage) as ImageView).setOnClickListener { view ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent);
        }

        setSupportActionBar(findViewById(R.id.my_toolbar))
        update()

        val recyclerView = findViewById<RecyclerView>(R.id.edit_recycler)

        findViewById<Button>(R.id.buttonEditHome).setOnClickListener { view ->
            ApplicationClass.instance.task = ApplicationClass.instance.data
            update()
            if (ApplicationClass.instance.task.children.size > 0) {
                recyclerView.scrollToPosition(0)
            }
        }

        findViewById<Button>(R.id.buttonEditBack).setOnClickListener { view ->
            if (null != ApplicationClass.instance.task.parent) {
                ApplicationClass.instance.task = ApplicationClass.instance.task.parent!!
                update()
            }
        }

        findViewById<Button>(R.id.buttonAdd).setOnClickListener { view ->
            ApplicationClass.instance.task.children.add(ApplicationClass.instance.data.default())
            ApplicationClass.instance.data.reset()
            ApplicationClass.instance.data.setParents(null)
            update()
            recyclerView.scrollToPosition(ApplicationClass.instance.task.children.size-1)
        }

        findViewById<Button>(R.id.buttonUndoEdit).setOnClickListener { view ->
            ApplicationClass.instance.data.load(cacheDir.toString(), this)
            update()
            /*
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent);
            */
        }

        findViewById<Button>(R.id.buttonEndEdit).setOnClickListener { view ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent);
        }
    }

    override fun onPause() {
        super.onPause()

        val recyclerView = findViewById<RecyclerView>(R.id.edit_recycler)
        for (i in 0..recyclerView.childCount - 1)
        {
            val holder = recyclerView.getChildViewHolder(recyclerView.getChildAt(i)) as CustomAdapter.ViewHolder
            ApplicationClass.instance.task.children[holder.layoutPosition].text = holder.textEditView.text.toString()
            ApplicationClass.instance.task.children[holder.layoutPosition].sendSMS = holder.sendSMSView.isChecked
            ApplicationClass.instance.task.children[holder.layoutPosition].type = if(holder.taskChipView.isChecked) ActionType.TASK else ActionType.ACTION
        }
        ApplicationClass.instance.data.save(cacheDir.toString(), this)
    }

    override fun onResume() {
        super.onResume()

        update()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.home -> {
                ApplicationClass.instance.data.reset()
                ApplicationClass.instance.data.setParents(null)
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent);
            }
            R.id.editTasks -> {
                ApplicationClass.instance.data.reset()
                ApplicationClass.instance.data.setParents(null)
                val intent = Intent(this, EditActivity::class.java)
                startActivity(intent);
            }
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent);
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun update()
    {
        title = ApplicationClass.instance.task.text
        (findViewById(R.id.toolBarTitle) as TextView).text = title

        //val view = findViewById<ConstraintLayout>(R.id.edit)
        //val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        //inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0)

        val recyclerView = findViewById<RecyclerView>(R.id.edit_recycler)

        // this creates a vertical layout Manager
        if (recyclerView.layoutManager == null) {
            recyclerView.layoutManager = LinearLayoutManager(this)
        }

        // This will pass the ArrayList to our Adapter
        if (recyclerView.adapter == null) {
            adapter = CustomAdapter(ApplicationClass.instance.task.children, ::update, true)

            // Setting the Adapter with the recyclerview
            recyclerView.adapter = adapter
        }
        else
        {
            adapter.updateList(ApplicationClass.instance.task.children)
        }

        if (itemTouchHelper != null) {
            itemTouchHelper?.attachToRecyclerView(null)
            itemTouchHelper = null
        }

        itemTouchHelper = adapter.setTouchHelper(adapter, adapter.editable)
        itemTouchHelper?.attachToRecyclerView(recyclerView)

        // that data has been updated.
        adapter.notifyDataSetChanged()
    }
}