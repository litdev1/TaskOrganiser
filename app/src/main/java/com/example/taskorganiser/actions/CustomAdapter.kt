package com.example.taskorganiser.actions

import android.app.Activity
import android.graphics.Color
import android.os.Build
import android.telephony.SmsManager
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.taskorganiser.ApplicationClass
import com.example.taskorganiser.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import java.util.Collections

class CustomAdapter(val mList: ArrayList<Action>,
                    val update: () -> (Unit),
                    val recyclerView: RecyclerView,
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
        holder.sendTextView.visibility = if(editable) View.VISIBLE else View.GONE
        holder.chipGroupView.visibility = if(editable) View.VISIBLE else View.GONE
        return holder
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val actionViewModel = mList[position]
        actionViewModel.image = if(actionViewModel.type == ActionType.TASK) R.drawable.ic_task_24dp else R.drawable.ic_action_24dp

        // sets the image to the imageview from our itemHolder class
        holder.imageView.setImageResource(actionViewModel.image)

        // sets the text to the textview from our itemHolder class
        holder.textView.text = actionViewModel.text
        holder.textEditView.setText(actionViewModel.text)
        holder.sendTextView.isChecked = actionViewModel.sendText
        holder.taskChipView.isChecked = actionViewModel.type == ActionType.TASK
        holder.actionChipView.isChecked = actionViewModel.type != ActionType.TASK

        when (actionViewModel.state) {
            StateType.NONE -> holder.layoutView.setBackgroundColor(Color.WHITE)
            StateType.DONE -> holder.layoutView.setBackgroundColor(Color.RED)
            StateType.YES -> holder.layoutView.setBackgroundColor(Color.GREEN)
            StateType.NO -> holder.layoutView.setBackgroundColor(Color.BLUE)
            else -> holder.layoutView.setBackgroundColor(Color.WHITE)
        }

        //Click
        holder.itemView.setOnClickListener { view ->
            val position = holder.layoutPosition
            val action = mList[position]
            action.state = StateType.DONE
            notifyItemChanged(position)
            if (action.type == ActionType.TASK /*&& action.children.isNotEmpty()*/)
            {
                ApplicationClass.instance.task = action
                update()
            }
            if (!editable && action.sendText) {
                val phoneNumber = "07881432137"
                val message = action.text + " was pressed"
                try {
                    val smsManager = view.context.getSystemService(SmsManager::class.java)
                    smsManager.sendTextMessage(phoneNumber, null, message, null, null)
                    Toast.makeText(view.context, message, 2000).show()
                }
                catch (e: Exception)
                {
                    Toast.makeText(view.context, message+" FAILED", 2000).show()
                }
            }
        }

        //Edit
        if (editable) {
            holder.textEditView.setOnFocusChangeListener { view, hasFocus ->
                if (!hasFocus) {
                    val editText = view as EditText
                    mList[position].text = editText.text.toString()
                }
            }
            holder.sendTextView.setOnClickListener { view ->
                val checkBox = view as CheckBox
                mList[position].sendText = checkBox.isChecked
            }
            holder.taskChipView .setOnClickListener { view ->
                val chip = view as Chip
                mList[position].type = if(chip.isChecked) ActionType.TASK else ActionType.ACTION
                notifyItemChanged(position)
            }
            holder.actionChipView .setOnClickListener { view ->
                val chip = view as Chip
                mList[position].type = if(chip.isChecked) ActionType.ACTION else ActionType.TASK
                notifyItemChanged(position)
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
        val sendTextView: CheckBox = itemView.findViewById(R.id.checkBoxText)
        val chipGroupView: ChipGroup = itemView.findViewById(R.id.chipGroupAction)
        val taskChipView: Chip = itemView.findViewById(R.id.chipTask)
        val actionChipView: Chip = itemView.findViewById(R.id.chipAction)
    }

    fun setTouchHelper(adapter: CustomAdapter, recyclerview: RecyclerView, editable: Boolean) : ItemTouchHelper {
        // on below line we are creating a method to create item touch helper
        // method for adding swipe to delete functionality.
        // in this we are specifying drag direction and position
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {

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

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // this method is called when we swipe our item.
                // on below line we are getting the item at a particular position.

                val actionItem: Action = adapter.mList.get(viewHolder.adapterPosition)

                // below line is to get the position
                // of the item at that position.
                val position = viewHolder.adapterPosition

                if (direction == ItemTouchHelper.LEFT) {

                    // this method is called when item is swiped.
                    // below line is to remove item from our array list.
                    adapter.mList.removeAt(viewHolder.adapterPosition)

                    // below line is to notify our item is removed from adapter.
                    adapter.notifyItemRemoved(viewHolder.adapterPosition)

                    if (editable) {
                        // below line is to display our snackbar with action.
                        Snackbar.make(recyclerview, "Deleted " + actionItem.text, 2000)
                            .setAction(
                                "Undo",
                                View.OnClickListener {
                                    // adding on click listener to our action of snack bar.
                                    // below line is to add our item to array list with a position.
                                    adapter.mList.add(position, actionItem)

                                    // below line is to notify item is
                                    // added to our adapter class.
                                    adapter.notifyItemInserted(position)
                                }).show()
                    }
                    else
                    {
                        actionItem.state = StateType.NONE
                        adapter.mList.add(position, actionItem)
                        adapter.notifyItemInserted(position)
                    }
                } else {
                    /*
                    actionItem.state = StateType.YES
                    actionItem.image = R.drawable.ic_dashboard_black_24dp
                    adapter.notifyItemChanged(position)
                    */
                }
            }
            // at last we are adding this
            // to our recycler view.
        })
        return itemTouchHelper
    }
}
