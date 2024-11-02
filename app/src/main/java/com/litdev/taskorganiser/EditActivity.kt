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
import com.litdev.taskorganiser.actions.CustomAdapter
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
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

        (findViewById<ImageView>(R.id.toolBarImage)!!).setOnClickListener { view ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        (findViewById<TextView>(R.id.toolBarTitle)!!).setOnClickListener { view ->
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        setSupportActionBar(findViewById(R.id.my_toolbar))
        update()

        val recyclerView = findViewById<RecyclerView>(R.id.edit_recycler)

        findViewById<Button>(R.id.buttonEditHome).setOnClickListener { view ->
            saveEdits()
            ApplicationClass.instance.task = ApplicationClass.instance.data
            update()
            if (ApplicationClass.instance.task.children.isNotEmpty()) {
                recyclerView.scrollToPosition(0)
            }
        }

        findViewById<Button>(R.id.buttonEditBack).setOnClickListener { view ->
            if (null != ApplicationClass.instance.task.parent) {
                saveEdits()
                ApplicationClass.instance.task = ApplicationClass.instance.task.parent!!
                update()
            }
        }

        findViewById<Button>(R.id.buttonAdd).setOnClickListener { view ->
            saveEdits()
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
            startActivity(intent)
        }
    }

    override fun onPause() {
        super.onPause()

        saveEdits()
        ApplicationClass.instance.data.save(cacheDir.toString(), this)
    }

    fun saveEdits()
    {
        val recyclerView = findViewById<RecyclerView>(R.id.edit_recycler)
        (recyclerView.adapter as CustomAdapter).saveEdits()
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
                startActivity(intent)
            }
            R.id.editTasks -> {
                ApplicationClass.instance.data.reset()
                ApplicationClass.instance.data.setParents(null)
                val intent = Intent(this, EditActivity::class.java)
                startActivity(intent)
            }
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.extra -> {
                val intent = Intent(this, ExtraActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun update()
    {
        title = ApplicationClass.instance.task.text
        (findViewById<TextView>(R.id.toolBarTitle)!!).text = title

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
            for (i in 0..recyclerView.childCount - 1) {
                val holder =
                    recyclerView.getChildViewHolder(recyclerView.getChildAt(i)) as CustomAdapter.ViewHolder
                hideKeyboard(holder.textEditView)
            }
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

    fun hideKeyboard(view: View) {
        val inputManager = view.context.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view.windowToken, 0)
    }
}