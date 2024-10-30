package com.litdev.taskorganiser

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ExtraActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_extra)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.extra)) { v, insets ->
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
        (findViewById<TextView>(R.id.toolBarTitle)!!).text = title

        // Check if the fragment is already added
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, ExportFragment())
                .commit()
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
}