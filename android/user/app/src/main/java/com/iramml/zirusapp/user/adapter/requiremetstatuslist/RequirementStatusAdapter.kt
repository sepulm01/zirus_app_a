package com.iramml.zirusapp.user.adapter.requiremetstatuslist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.adapter.ClickListener
import com.iramml.zirusapp.user.model.Requirement


class RequirementStatusAdapter(var context: Context?, private var items: ArrayList<Requirement>, private var listener: ClickListener) : RecyclerView.Adapter<RequirementStatusViewHolder>() {
    private var viewHolder: RequirementStatusViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequirementStatusViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.template_requirement_item, parent, false)
        viewHolder = RequirementStatusViewHolder(view, listener)
        return viewHolder!!
    }
    override fun onBindViewHolder(holder: RequirementStatusViewHolder, position: Int) {
        val item: Requirement = items[position]
        viewHolder?.tvTitle?.text = item.id
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onViewRecycled(holder: RequirementStatusViewHolder) {
        super.onViewRecycled(holder)
    }

}
