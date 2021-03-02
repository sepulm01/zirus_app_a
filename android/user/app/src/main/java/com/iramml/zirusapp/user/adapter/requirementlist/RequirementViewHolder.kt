package com.iramml.zirusapp.user.adapter.requirementlist

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.adapter.ClickListener

class RequirementViewHolder(itemView: View, var listener: ClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener  {
    var viewStatus: View = itemView.findViewById(R.id.view_status)
    var tvTitle: TextView = itemView.findViewById(R.id.tv_title)
    var tvDate: TextView = itemView.findViewById(R.id.tv_date)

    override fun onClick(view: View) {
        listener.onClick(view, adapterPosition)
    }

    init {
        itemView.setOnClickListener(this)
    }
}