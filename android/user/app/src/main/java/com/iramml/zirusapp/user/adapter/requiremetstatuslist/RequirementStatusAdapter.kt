package com.iramml.zirusapp.user.adapter.requiremetstatuslist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.vipulasri.timelineview.TimelineView
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.adapter.ClickListener
import com.iramml.zirusapp.user.model.RequirementStatusItem


class RequirementStatusAdapter(var context: Context?, private var items: ArrayList<RequirementStatusItem>, private var listener: ClickListener) : RecyclerView.Adapter<RequirementStatusViewHolder>() {
    private var viewHolder: RequirementStatusViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequirementStatusViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.template_requirement_status_item, parent, false)
        viewHolder = RequirementStatusViewHolder(view, viewType, listener)
        return viewHolder!!
    }
    override fun onBindViewHolder(holder: RequirementStatusViewHolder, position: Int) {
        val item: RequirementStatusItem = items[position]
        viewHolder?.tvTitle?.text = item.name
        viewHolder?.tvDate?.text = item.date
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onViewRecycled(holder: RequirementStatusViewHolder) {
        super.onViewRecycled(holder)
    }

    override fun getItemViewType(position: Int): Int {
        return TimelineView.getTimeLineViewType(position, itemCount)
    }
}
