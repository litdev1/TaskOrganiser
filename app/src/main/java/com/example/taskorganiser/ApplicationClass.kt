package com.example.taskorganiser

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import com.example.taskorganiser.actions.Action
import com.example.taskorganiser.actions.ActionType
import com.example.taskorganiser.actions.Settings
import com.example.taskorganiser.actions.StateType
import java.io.File

class ApplicationClass: Application() {
    var firstTime: Boolean = true
    val version: Int = 1
    var canUseSMS: Boolean = false
    var settings: Settings = Settings(
        "",
        "",
        true
    )
    val data: Action = Action(
        "Home",
        ActionType.HOME,
        StateType.NONE,
        false,
        null,
        ArrayList<Action>())
    var task: Action = data

    companion object {
        lateinit var instance: com.example.taskorganiser.ApplicationClass
            private set
    }

    override fun onCreate() {
        super.onCreate()

        if (File(cacheDir, "version").exists())
        {
            firstTime = false
        }

        instance = this
        data.load(cacheDir.toString(), this)
    }
}