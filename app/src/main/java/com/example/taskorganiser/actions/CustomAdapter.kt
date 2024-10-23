package com.example.taskorganiser.actions

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskorganiser.ApplicationClass
import com.example.taskorganiser.MainActivity
import com.example.taskorganiser.R

class CustomAdapter(val mList: ArrayList<Action>, val activity: MainActivity) : RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

    // create new views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // inflates the card_view_design view
        // that is used to hold list item
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view, parent, false)

        return ViewHolder(view)
    }

    // binds the list items to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val actionViewModel = mList[position]

        // sets the image to the imageview from our itemHolder class
        holder.imageView.setImageResource(actionViewModel.image)

        // sets the text to the textview from our itemHolder class
        holder.textView.text = actionViewModel.text

        when (actionViewModel.state) {
            StateType.NONE -> holder.layoutView.setBackgroundColor(Color.WHITE)
            StateType.DONE -> holder.layoutView.setBackgroundColor(Color.RED)
            StateType.YES -> holder.layoutView.setBackgroundColor(Color.GREEN)
            StateType.NO -> holder.layoutView.setBackgroundColor(Color.BLUE)
            else -> holder.layoutView.setBackgroundColor(Color.WHITE)
        }

        holder.itemView.setOnClickListener { view ->
            val position = holder.layoutPosition
            val action = mList[position]
            action.state = StateType.DONE
            notifyItemChanged(position)
            if (action.type == ActionType.TASK && action.children.isNotEmpty())
            {
                ApplicationClass.instance.task = action
                activity.update()
            }
        }

        holder.textView.setOnFocusChangeListener { view, hasFocus ->
            if (!hasFocus) {
                val editText = view as TextView
                mList[position].text = editText.text.toString()
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
        val layoutView: LinearLayout = itemView.findViewById(R.id.layoutView)
    }
}
