package com.iramml.zirusapp.user.adapter.requirementlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.adapter.ClickListener
import com.iramml.zirusapp.user.model.Requirement


class RequirementsAdapter(var context: Context?, private var items: ArrayList<Requirement>, private var listener: ClickListener) : RecyclerView.Adapter<RequirementViewHolder>() {
    private var viewHolder: RequirementViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequirementViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.template_requirement_item, parent, false)
        viewHolder = RequirementViewHolder(view, listener)
        return viewHolder!!
    }
    override fun onBindViewHolder(holder: RequirementViewHolder, position: Int) {
        val item: Requirement = items[position]
        viewHolder?.tvTitle?.text = item.id
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onViewRecycled(holder: RequirementViewHolder) {
        super.onViewRecycled(holder)
    }

}
