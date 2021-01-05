package com.iramml.zirusapp.user.adapter.categorylist

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.adapter.ClickListener

class CategoriesViewHolder(itemView: View, var listener: ClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener  {
    var tvName: TextView = itemView.findViewById(R.id.tv_name)
    var ivIcon: ImageView = itemView.findViewById(R.id.iv_icon)

    override fun onClick(view: View) {
        listener.onClick(view, adapterPosition)
    }

    init {
        itemView.setOnClickListener(this)
    }
}