package com.litdev.taskorganiser

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.settings)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        (findViewById<TextView>(R.id.info)!!).movementMethod = ScrollingMovementMethod()

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

        findViewById<EditText>(R.id.editTextUser).setText(ApplicationClass.instance.settings.user)
        findViewById<EditText>(R.id.editTextPhone).setText(ApplicationClass.instance.settings.phone)
        findViewById<CheckBox>(R.id.checkBoxUseSMS).isChecked = ApplicationClass.instance.settings.useSMS
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

        ApplicationClass.instance.settings.user = findViewById<EditText>(R.id.editTextUser).text.toString()
        ApplicationClass.instance.settings.phone = findViewById<EditText>(R.id.editTextPhone).text.toString()
        ApplicationClass.instance.settings.useSMS = findViewById<CheckBox>(R.id.checkBoxUseSMS).isChecked

        try {
            BufferedOutputStream(FileOutputStream(File(cacheDir, "settings.json"))).use { bos ->
                val json = Json.encodeToString(ApplicationClass.instance.settings)
                bos.write(json.toByteArray())
            }
        }
        catch (e: Exception)
        {
            Toast.makeText(this, "Saving settings failed", Toast.LENGTH_LONG).show()
        }
    }
}