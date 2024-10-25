package com.example.taskorganiser

import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
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

        findViewById<EditText>(R.id.editTextUser).setText(ApplicationClass.instance.settings.user)
        findViewById<EditText>(R.id.editTextPhone).setText(ApplicationClass.instance.settings.phone)
        findViewById<CheckBox>(R.id.checkBoxUseSMS).isChecked = ApplicationClass.instance.settings.useSMS
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
            Toast.makeText(this, "Saving settings data failed", 2000).show()
        }
    }
}