package com.iramml.zirusapp.user.adapter.requiremetstatuslist

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.adapter.ClickListener

class RequirementStatusViewHolder(itemView: View, var listener: ClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener  {
    var tvTitle: TextView = itemView.findViewById(R.id.tv_title)

    override fun onClick(view: View) {
        listener.onClick(view, adapterPosition)
    }

    init {
        itemView.setOnClickListener(this)
    }
}