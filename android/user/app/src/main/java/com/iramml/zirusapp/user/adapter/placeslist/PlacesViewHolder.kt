package com.iramml.zirusapp.user.adapter.placeslist

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.adapter.ClickListener

class PlacesViewHolder(itemView: View, var listener: ClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener  {
    var tvPlaceName: TextView = itemView.findViewById(R.id.tv_place_name)

    override fun onClick(view: View) {
        listener.onClick(view, adapterPosition)
    }

    init {
        itemView.setOnClickListener(this)
    }
}