package com.litdev.taskorganiser.actions

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.litdev.taskorganiser.ApplicationClass
import com.litdev.taskorganiser.R

val messages = mutableMapOf("start" to "This is an app to organise tasks and tick them off as they are done." +
        "\n\nYou can return to the main Home screen and reset all tasks to uncompleted by clicking the app icon on the top left or using the options menu on the top right.",
    "task" to "You clicked a task, which contains sub tasks and actions.",
    "action" to "You completed an action." +
            "\n\nActions and tasks can be reverted with a left swipe.",
    "sms" to "You clicked an item that can send an SMS." +
            "\n\nThis requires a telephone number to be entered on the settings screen.",
    "home" to "This will navigate you to the Home screen, without any tasks or actions being reset.",
    "back" to "This will navigate you to the parent task, without any tasks or actions being reset.",
    "edit" to "You can edit tasks and actions here." +
            "\n\n► \'Add\' to add an action to the end of the current list" +
            "\n► Delete (left swipe)" +
            "\n► Re-order (drag and drop)" +
//            "\n► Set the task or action type, text description and optional SMS" +
            "\n► \'Undo\' to undo all editing" +
            "\n► \'Done\' to save and end editing",
    "reset" to "This will navigate you to the Home screen, resetting all tasks and actions.",
    "add" to "A new action is added to the end of the current task list.",
    "undo" to "All edits in the current session have been reverted.",
    "end" to "All edits saved and edit session ended.",
    "swipeTask" to "This task and all sub-actions have been reset.",
    "swipeAction" to "This action has been reset.",
)

fun saveMessages() {
    val context = ApplicationClass.instance
    val sharedPreferences = context.getSharedPreferences("Messages", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    for ((key, value) in messages) {
        editor.putString(key, value)
    }
    editor.commit()
}

fun loadMessages() {
    val context = ApplicationClass.instance
    val sharedPreferences = context.getSharedPreferences("Messages", Context.MODE_PRIVATE)
    for ((key, value) in messages) {
        messages[key] = sharedPreferences.getString(key, value).toString()
    }
}

fun showMessage(context: Context, key: String) {
    if (!messages[key].isNullOrEmpty()) {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogStyle)
        builder.setMessage(messages[key])
        builder.setPositiveButton("Got it", { dialog, i: Int ->
            messages[key] = ""
            saveMessages()
        })
        val dialog = builder.create()
        dialog.show()
    }
}
