package com.example.taskorganiser

import android.app.Application
import com.example.taskorganiser.actions.Action
import com.example.taskorganiser.actions.ActionType
import com.example.taskorganiser.actions.StateType

class ApplicationClass: Application() {
    val version: Int = 1
    val data: Action = Action(R.drawable.ic_home_black_24dp,
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

        instance = this
        data.load(cacheDir.toString())
    }
}