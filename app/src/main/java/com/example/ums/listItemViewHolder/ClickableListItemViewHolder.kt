package com.example.ums.listItemViewHolder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ums.R

class ClickableListItemViewHolder (itemView : View) : RecyclerView.ViewHolder(itemView) {

    val firstTextView : TextView = itemView.findViewById(R.id.first_text_view)
    val secondTextView : TextView = itemView.findViewById(R.id.second_text_view)
}