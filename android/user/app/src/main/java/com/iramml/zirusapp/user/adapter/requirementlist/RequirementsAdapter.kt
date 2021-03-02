package com.iramml.zirusapp.user.adapter.requirementlist

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.adapter.ClickListener
import com.iramml.zirusapp.user.model.schema.firebase.Requirement


class RequirementsAdapter(var context: Context?, private var items: List<Requirement>, private var listener: ClickListener) : RecyclerView.Adapter<RequirementViewHolder>() {
    private var viewHolder: RequirementViewHolder? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequirementViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.template_requirement_item, parent, false)
        viewHolder = RequirementViewHolder(view, listener)
        return viewHolder!!
    }
    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RequirementViewHolder, position: Int) {
        val item: Requirement = items[position]
        viewHolder?.tvTitle?.text = context!!.getString(R.string.requirement_number) + " " + item.requirement_num
        viewHolder?.tvDate?.text = item.dateTime

        when (item.status) {
            "sent" -> viewHolder?.viewStatus?.background = context!!.getDrawable(R.drawable.shape_sent_status)
            "seen" -> viewHolder?.viewStatus?.background = context!!.getDrawable(R.drawable.shape_received_status)
            "in_progress" -> viewHolder?.viewStatus?.background = context!!.getDrawable(R.drawable.shape_in_progress_status)
            "done" -> viewHolder?.viewStatus?.background = context!!.getDrawable(R.drawable.shape_done_status)
            else -> viewHolder?.viewStatus?.background = context!!.getDrawable(R.drawable.shape_done_status)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onViewRecycled(holder: RequirementViewHolder) {
        super.onViewRecycled(holder)
    }

}
