package com.example.handleliste

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

data class ListItem(val id: Int, val title: String, var isChecked: Boolean = false)

class ListAdapter(private val items: MutableList<ListItem>) : RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.text_view_item)
        val checkBox: CheckBox = view.findViewById(R.id.check_box)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.textView.text = item.title
        holder.checkBox.isChecked = item.isChecked

        updateTextAppearance(holder.textView, item.isChecked)

        holder.checkBox.setOnClickListener {
            item.isChecked = !item.isChecked
            updateTextAppearance(holder.textView, item.isChecked)
        }

        holder.itemView.setOnClickListener {
            holder.checkBox.performClick()
        }
    }

    override fun getItemCount() = items.size

    private fun updateTextAppearance(textView: TextView, isChecked: Boolean) {
        if (isChecked) {
            textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            textView.setTextColor(Color.RED)
        } else {
            textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            textView.setTextColor(Color.WHITE) // Use your default text color
        }
    }

    fun removeCheckedItems() {
        items.removeAll { it.isChecked }
        notifyDataSetChanged()
    }
}
