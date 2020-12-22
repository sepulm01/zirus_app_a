package com.iramml.zirusapp.user.adapter.placeslist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.adapter.ClickListener
import com.iramml.zirusapp.user.model.firebase.Requirement
import com.iramml.zirusapp.user.model.googleapis.PlacesResult


class PlacesAdapter(var context: Context?, private var items: ArrayList<PlacesResult>, private var listener: ClickListener) : RecyclerView.Adapter<PlacesViewHolder>() {
    private var viewHolder: PlacesViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlacesViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.template_place_item, parent, false)
        viewHolder = PlacesViewHolder(view, listener)
        return viewHolder!!
    }
    override fun onBindViewHolder(holder: PlacesViewHolder, position: Int) {
        val item: PlacesResult = items[position]
        viewHolder?.tvPlaceName?.text = item.formatted_address
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onViewRecycled(holder: PlacesViewHolder) {
        super.onViewRecycled(holder)
    }

}
