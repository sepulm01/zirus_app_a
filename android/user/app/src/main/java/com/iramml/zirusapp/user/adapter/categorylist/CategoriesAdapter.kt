package com.iramml.zirusapp.user.adapter.categorylist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.adapter.ClickListener
import com.iramml.zirusapp.user.model.schema.firebase.RequirementCategory
import com.squareup.picasso.Picasso


class CategoriesAdapter(var context: Context?, private var items: List<RequirementCategory>, private var listener: ClickListener) : RecyclerView.Adapter<CategoriesViewHolder>() {
    private var viewHolder: CategoriesViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriesViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.template_category_item, parent, false)
        viewHolder = CategoriesViewHolder(view, listener)
        return viewHolder!!
    }
    override fun onBindViewHolder(holder: CategoriesViewHolder, position: Int) {
        val item: RequirementCategory = items[position]
        viewHolder?.tvName?.text = item.name
        Picasso.get()
                .load(item.icon)
                .into(viewHolder?.ivIcon)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onViewRecycled(holder: CategoriesViewHolder) {
        super.onViewRecycled(holder)
    }

}
