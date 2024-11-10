package com.litdev.taskorganiser

import android.app.Application
import android.content.Context
import com.litdev.taskorganiser.actions.Action
import com.litdev.taskorganiser.actions.ActionType
import com.litdev.taskorganiser.actions.Settings
import com.litdev.taskorganiser.actions.loadMessages
import com.litdev.taskorganiser.actions.saveMessages
import java.io.File
import kotlin.apply

class ApplicationClass: Application() {
    var firstTime: Boolean = true
    val version: Int = 1
    var canUseSMS: Boolean = false
    var settings: Settings = Settings(
        "",
        true
    )
    val data: Action = Action(
        "Home",
        ActionType.HOME,
        false,
        ArrayList<Action>())
    var task: Action = data

    companion object {
        lateinit var instance: ApplicationClass
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
        loadMessages()
    }
}