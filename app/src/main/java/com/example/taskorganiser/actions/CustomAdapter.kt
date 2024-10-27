package com.example.taskorganiser.actions

import android.content.res.Configuration
import android.graphics.Color
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.toColor
import androidx.core.graphics.toColorLong
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.taskorganiser.ApplicationClass
import com.example.taskorganiser.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.color.MaterialColors
import com.google.android.material.snackbar.Snackbar
import java.util.Collections

class CustomAdapter(var mList: ArrayList<Action>,
                    val update: () -> (Unit),
                    val editable: Boolean) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view, parent, false)

        val holder = ViewHolder(view)
        holder.textView.visibility = if(editable) View.GONE else View.VISIBLE
        holder.textEditView.visibility = if(editable) View.VISIBLE else View.GONE
        holder.sendSMSView.visibility = if(editable) View.VISIBLE else View.GONE
        holder.chipGroupView.visibility = if(editable) View.VISIBLE else View.GONE
        return holder
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val action = mList[position]

            setControls(holder, action, position)
            setVisuals(holder, action, position)
            setEvents(holder)
        }
        catch (e: Exception)
        {
            val a = 1
        }
    }

    fun updateList(mList: ArrayList<Action>)
    {
        this.mList = mList
    }

    fun setControls(holder: ViewHolder, action: Action, position: Int)
    {
        val currentNightMode = holder.layoutView.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        // sets the image to the imageview from our itemHolder class
        if (editable || action.state == StateType.NONE) {
            val image = if (action.type == ActionType.TASK) R.drawable.ic_task_24dp else R.drawable.ic_action_24dp
            holder.imageView.setImageResource(image)
        } else {
            when (currentNightMode) {
                Configuration.UI_MODE_NIGHT_NO -> {
                    val image = if (action.type == ActionType.TASK) R.drawable.ic_task_done_24dp else R.drawable.ic_action_done_24dp
                    holder.imageView.setImageResource(image)
                }
                Configuration.UI_MODE_NIGHT_YES -> {
                    val image = if (action.type == ActionType.TASK) R.drawable.ic_task_done_dark_24dp else R.drawable.ic_action_done_dark_24dp
                    holder.imageView.setImageResource(image)
                }
            }
        }

        // sets the text to the textview from our itemHolder class
        holder.textView.text = action.text
        holder.textEditView.setText(action.text)

        // sets checkboxes
        holder.sendSMSView.isChecked = action.sendSMS
        holder.chipGroupView.check(if (action.type == ActionType.TASK) holder.taskChipView.id else holder.actionChipView.id)
    }

    fun setVisuals(holder: ViewHolder, action: Action, position: Int)
    {
        val currentNightMode = holder.layoutView.context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

        // set card colour
        var color = 0
        var colorSelected = 0
        when (currentNightMode) {
            Configuration.UI_MODE_NIGHT_NO -> {
                color = ContextCompat.getColor(holder.layoutView.context,
                    if (action.type == ActionType.TASK) R.color.tasksLight else R.color.actionsLight)
                colorSelected = ColorUtils.blendARGB(color, Color.BLACK, 0.2f)
            } // Night mode is not active, we're using the light theme
            Configuration.UI_MODE_NIGHT_YES -> {
                color = ContextCompat.getColor(holder.layoutView.context,
                    if (action.type == ActionType.TASK) R.color.tasksDark else R.color.actionsDark)
                colorSelected = ColorUtils.blendARGB(color, Color.WHITE, 0.2f)
            } // Night mode is active, we're using dark theme
        }
        if (editable) {
            holder.layoutView.setBackgroundColor(color)
        } else {
            when (action.state) {
                StateType.NONE -> holder.layoutView.setBackgroundColor(color)
                StateType.DONE -> holder.layoutView.setBackgroundColor(colorSelected)
                StateType.YES -> holder.layoutView.setBackgroundColor(colorSelected)
                StateType.NO -> holder.layoutView.setBackgroundColor(colorSelected)
            }
        }
    }

    fun setEvents(holder: ViewHolder)
    {
        //Click events
        holder.itemView.setOnClickListener { view ->
            val position = holder.layoutPosition
            val action = mList[position]
            val oldState = action.state
            action.state = StateType.DONE
            if (!editable && oldState != action.state) {
                notifyItemChanged(position)
//                setControls(holder, action, position)
//                setVisuals(holder, action, position)
            }
            if (action.type == ActionType.TASK && (editable || action.children.isNotEmpty())) {
                ApplicationClass.instance.task = action
                update()
            }
            if (action.type == ActionType.ACTION && !editable && action.parent != null) {
                val parent = action.parent
                if (parent?.children?.last() == action && parent?.parent != null) {
                    ApplicationClass.instance.task = parent.parent!!
                    update()
                }
            }
            if (ApplicationClass.instance.canUseSMS &&
                ApplicationClass.instance.settings.useSMS &&
                !editable &&
                oldState == StateType.NONE && action.sendSMS) {
                val phoneNumber = ApplicationClass.instance.settings.phone
                val message = ApplicationClass.instance.settings.user + " completed " +
                        if(action.type == ActionType.TASK) "task" else "action" + " : " + action.text
                try {
                    val smsManager = view.context.getSystemService(SmsManager::class.java)
                    smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                    //Toast.makeText(view.context, message, Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Toast.makeText(view.context, "SMS failed", Toast.LENGTH_LONG).show()
                }
            }
        }

        //Edit events
        if (editable) {
            holder.textEditView.setOnFocusChangeListener { view, hasFocus ->
                try {
                    val position = holder.layoutPosition
                    val action = mList[position]
                    if (!hasFocus) {
                        val editText = view as EditText
                        action.text = editText.text.toString()
                    }
                }
                catch (e: Exception)
                {
                    val a = 1;
                }
            }
            holder.sendSMSView.setOnClickListener { view ->
                val position = holder.layoutPosition
                val action = mList[position]
                val checkBox = view as CheckBox
                action.sendSMS = checkBox.isChecked
            }
            holder.chipGroupView.setOnCheckedStateChangeListener() { group, checkedIds ->
                val position = holder.layoutPosition
                val action = mList[position]
                if (checkedIds.size > 0) {
                    var newType =
                        if (checkedIds[0] == holder.taskChipView.id) ActionType.TASK else ActionType.ACTION
                    if (action.type != newType) {
                        action.type = newType
                        notifyItemChanged(position)
//                        setControls(holder, action, position)
//                        setVisuals(holder, action, position)
                    }
                }
            }
        }
    }

    // return the number of the items in the list
    override fun getItemCount(): Int {
        return mList.size
    }

    // Holds the views for adding it to image and text
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val textView: TextView = itemView.findViewById(R.id.textView)
        val textEditView: EditText = itemView.findViewById(R.id.textEditView)
        val layoutView: LinearLayout = itemView.findViewById(R.id.layoutView)
        val sendSMSView: CheckBox = itemView.findViewById(R.id.checkBoxSMS)
        val chipGroupView: ChipGroup = itemView.findViewById(R.id.chipGroupAction)
        val taskChipView: Chip = itemView.findViewById(R.id.chipTask)
        val actionChipView: Chip = itemView.findViewById(R.id.chipAction)
    }

    fun setTouchHelper(adapter: CustomAdapter, editable: Boolean) : ItemTouchHelper {
        // on below line we are creating a method to create item touch helper
        // method for adding swipe to delete functionality.
        // in this we are specifying drag direction and position
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                try {
                    if (!editable) return false
                    // this method is called
                    // when the item is moved.
                    //return false
                    val from = viewHolder.adapterPosition
                    val to = target.adapterPosition
                    Collections.swap(adapter.mList, from, to)
                    adapter.notifyItemMoved(from, to)
                    return true
                }
                catch (e: Exception)
                {
                    return false
                }
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // this method is called when we swipe our item.
                // on below line we are getting the item at a particular position.
                try {
                    val position = viewHolder.adapterPosition
                    val action: Action = adapter.mList.get(position)

                    if (direction == ItemTouchHelper.LEFT) {

                        // this method is called when item is swiped.
                        // below line is to remove item from our array list.
                        adapter.mList.removeAt(position)

                        // below line is to notify our item is removed from adapter.
                        adapter.notifyItemRemoved(position)

                        if (editable) {
                            // below line is to display our snackbar with action.
                            val snackBar = Snackbar.make(viewHolder.itemView, "Deleted " + action.text, Snackbar.LENGTH_LONG)
                            /*
                            val layoutParams = snackBar.view.layoutParams as CoordinatorLayout.LayoutParams
                            layoutParams.anchorId = R.id.navigation //Id for your bottomNavBar or TabLayout
                            layoutParams.anchorGravity = Gravity.TOP
                            layoutParams.gravity = Gravity.TOP
                            snackBar.view.layoutParams = layoutParams
                            */
                            snackBar.setAnchorView(viewHolder.itemView)
                            snackBar.setAction(
                                "Undo",
                                View.OnClickListener {
                                    // adding on click listener to our action of snack bar.
                                    // below line is to add our item to array list with a position.
                                    adapter.mList.add(position, action)
                                    // below line is to notify item is
                                    // added to our adapter class.
                                    adapter.notifyItemInserted(position)
                                }).show()
                        }
                        else
                        {
                            action.state = StateType.NONE
                            adapter.mList.add(position, action)
                            adapter.notifyItemInserted(position)
                        }
                    } else {
                        /*
                        actionItem.state = StateType.YES
                        actionItem.image = R.drawable.ic_dashboard_24dp
                        adapter.notifyItemChanged(position)
                        */
                    }
                }
                catch (e: Exception)
                {
                    val a = 1
                }
            }
        })
        return itemTouchHelper
    }
}
