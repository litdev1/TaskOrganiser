package com.litdev.taskorganiser.actions

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import com.litdev.taskorganiser.ApplicationClass
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
data class Action(var text: String,
                  var type: ActionType,
                  var state: StateType,
                  var sendSMS: Boolean,
                  var parent: Action?,
                  val children: ArrayList<Action>)
{
    fun save(cacheDir: String, context: Context?)
    {
        reset()
        try {
            BufferedOutputStream(FileOutputStream(File(cacheDir, "version"))).use { bos ->
                bos.write(ApplicationClass.instance.version.toString().toByteArray())
            }
            BufferedOutputStream(FileOutputStream(File(cacheDir, "settings.json"))).use { bos ->
                val json = Json.encodeToString(ApplicationClass.instance.settings)
                bos.write(json.toByteArray())
            }
            BufferedOutputStream(FileOutputStream(File(cacheDir, "data.json"))).use { bos ->
                val json = serialise()
                bos.write(json.toByteArray())
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Saving action data failed", Toast.LENGTH_LONG).show()
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
                    InputStreamReader(FileInputStream(File(cacheDir, "settings.json"))).use { bos ->
                        val json = bos.readText()
                        ApplicationClass.instance.settings = Json.decodeFromString<Settings>(json)
                    }
                    InputStreamReader(FileInputStream(File(cacheDir, "data.json"))).use { bos ->
                        val json = bos.readText()
                        deserialise(json)
                    }
                }
            }
            reset()
            setParents(null)
        } catch (e: Exception) {
            //Toast.makeText(context, "Loading action data failed", Toast.LENGTH_LONG).show()
            initial()
            save(cacheDir, context)
        }
//        initial()
//        save(cacheDir, context)
        ApplicationClass.instance.task = this
    }

    fun initial()
    {
        children.clear()

        children.add(default())
        children[0].text = "Shopping"
        children[0].type = ActionType.TASK
        children[0].children.add(default()); children[0].children[0].text = "Wallet - check cards or cash"
        children[0].children.add(default()); children[0].children[1].text = "Keys"
        children[0].children.add(default()); children[0].children[2].text = "Glasses"
        children[0].children.add(default()); children[0].children[3].text = "Coat/umbrella"
        children[0].children.add(default()); children[0].children[4].text = "Bag for shopping"
        children[0].children.add(default()); children[0].children[5].text = "Shopping list"
        children[0].children.add(default()); children[0].children[6].text = "Lock door"

        children.add(default())
        children[1].text = "Shopping List"
        children[1].type = ActionType.TASK
        children[1].children.add(default()); children[1].children[0].text = "Bread"
        children[1].children.add(default()); children[1].children[1].text = "Milk"
        children[1].children.add(default()); children[1].children[2].text = "Cheese"
        children[1].children.add(default()); children[1].children[3].text = "Eggs"
        children[1].children.add(default()); children[1].children[4].text = "Potatoes"
        children[1].children.add(default()); children[1].children[5].text = "Onions"
        children[1].children.add(default()); children[1].children[6].text = "Biscuits"

        children.add(default())
        children[2].text = "SMS test"
        children[2].sendSMS = true
    }

    fun default() : Action {
        return Action(
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

