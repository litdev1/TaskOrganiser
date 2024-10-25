package com.example.taskorganiser.actions

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import com.example.taskorganiser.ApplicationClass
import com.example.taskorganiser.R
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStreamReader

@SuppressLint("UnsafeOptInUsageError")
@Serializable
data class Action(var image: Int,
                  var text: String,
                  var type: ActionType,
                  var state: StateType,
                  var sendText: Boolean,
                  var parent: Action?,
                  val children: ArrayList<Action>)
{
    fun save(cacheDir: String, context: Context)
    {
        reset()
        try {
            BufferedOutputStream(FileOutputStream(File(cacheDir, "version"))).use { bos ->
                bos.write(ApplicationClass.instance.version.toString().toByteArray())
            }
            val json = serialise()
            BufferedOutputStream(FileOutputStream(File(cacheDir, "data.json"))).use { bos ->
                bos.write(json.toByteArray())
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Saving data failed", 2000).show()
        }
        setParents(null)
    }

    fun load(cacheDir: String, context: Context)
    {
        try {
            InputStreamReader(FileInputStream(File(cacheDir, "version"))).use { bos ->
                val ver = bos.readText().toInt()
                if (ver <= ApplicationClass.instance.version)
                {
                    InputStreamReader(FileInputStream(File(cacheDir, "data.json"))).use { bos ->
                        val json = bos.readText()
                        deserialise(json)
                    }
                }
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Loading data failed", 2000).show()
            initial()
        }
        reset()
        setParents(null)
        ApplicationClass.instance.task = this
    }

    fun initial()
    {
        children.clear()
        for (i in 1..10) {
            children.add(Action(
                R.drawable.ic_action_24dp,
                "Action $i\nMore data",
                ActionType.ACTION,
                StateType.NONE,
                false,
                null,
                ArrayList<Action>()))
        }
        children[0].text = "Task"
        children[0].type = ActionType.TASK
        children[0].children.add(default())
    }

    fun default() : Action {
        return Action(
            R.drawable.ic_home_black_24dp,
            "Action",
            ActionType.ACTION,
            StateType.NONE,
            false,
            null,
            ArrayList<Action>())
    }

    fun serialise(): String
    {
        return Json.encodeToString(this)
    }

    fun deserialise(json : String)
    {
        val newData = Json.decodeFromString<Action>(json)
        children.clear()
        for (item in newData.children)
        {
            children.add(item)
        }
    }

    fun reset()
    {
        state = StateType.NONE
        parent = null
        if (type != ActionType.HOME && type != ActionType.TASK)
        {
            children.clear()
        }
        for (item in children)
        {
            item.reset();
        }
    }

    fun setParents(parent: Action?)
    {
        this.parent = parent
        for (item in children)
        {
            item.setParents(this);
        }
    }
}

