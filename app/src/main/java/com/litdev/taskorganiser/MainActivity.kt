package com.litdev.taskorganiser

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.litdev.taskorganiser.actions.CustomAdapter

class MainActivity : AppCompatActivity() {
    private val SEND_SMS_PERMISSION_CODE = 100
    var itemTouchHelper: ItemTouchHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (ApplicationClass.instance.firstTime)
        {
            ApplicationClass.instance.firstTime = false
//            val intent = Intent(this, SettingsActivity::class.java)
//            startActivity(intent)
        }

        (findViewById<ImageView>(R.id.toolBarImage)!!).setOnClickListener { view ->
            ApplicationClass.instance.data.reset()
            ApplicationClass.instance.data.setParents(null)
            ApplicationClass.instance.task = ApplicationClass.instance.data
            update()
            if (ApplicationClass.instance.task.children.isNotEmpty()) {
                val recyclerView = findViewById<RecyclerView>(R.id.main_recycler)
                recyclerView.scrollToPosition(0)
            }
        }
        (findViewById<TextView>(R.id.toolBarTitle)!!).setOnClickListener { view ->
            ApplicationClass.instance.data.reset()
            ApplicationClass.instance.data.setParents(null)
            ApplicationClass.instance.task = ApplicationClass.instance.data
            update()
            if (ApplicationClass.instance.task.children.isNotEmpty()) {
                val recyclerView = findViewById<RecyclerView>(R.id.main_recycler)
                recyclerView.scrollToPosition(0)
            }
        }

        // Check if the permission is already granted for SMS
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            if (ApplicationClass.instance.settings.useSMS) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.SEND_SMS),
                    SEND_SMS_PERMISSION_CODE
                )
            }
        } else {
            ApplicationClass.instance.canUseSMS = true
        }

        setSupportActionBar(findViewById(R.id.my_toolbar))
        update()

        findViewById<Button>(R.id.buttonMainHome).setOnClickListener { view ->
            ApplicationClass.instance.task = ApplicationClass.instance.data
            update()
            if (ApplicationClass.instance.task.children.isNotEmpty()) {
                val recyclerView = findViewById<RecyclerView>(R.id.main_recycler)
                recyclerView.scrollToPosition(0)
            }
        }

        findViewById<Button>(R.id.buttonMainBack).setOnClickListener { view ->
            if (null != ApplicationClass.instance.task.parent) {
//                ApplicationClass.instance.task.state = StateType.NONE
//                if (ApplicationClass.instance.task.isDone())
//                {
//                    ApplicationClass.instance.task.state = StateType.DONE
//                }
                ApplicationClass.instance.task = ApplicationClass.instance.task.parent!!
                update()
            }
        }
    }

    // Handle the permission request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SEND_SMS_PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                ApplicationClass.instance.canUseSMS = true
            } else {
                Toast.makeText(this, "App will be unable to send SMS texts", Toast.LENGTH_LONG).show()
            }
        }
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

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()

        update()
    }

    fun update()
    {
        title = ApplicationClass.instance.task.text
        (findViewById<TextView>(R.id.toolBarTitle)!!).text = title
        val recyclerView = findViewById<RecyclerView>(R.id.main_recycler)

        // this creates a vertical layout Manager
        if (recyclerView.layoutManager == null) {
            recyclerView.layoutManager = LinearLayoutManager(this)
        }

        // This will pass the ArrayList to our Adapter
        val adapter = CustomAdapter(ApplicationClass.instance.task.children, ::update, false)

        // Setting the Adapter with the recyclerview
        recyclerView.adapter = adapter

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