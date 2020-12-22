package com.iramml.zirusapp.user.adapter.requiremetstatuslist

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.vipulasri.timelineview.TimelineView
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.adapter.ClickListener


class RequirementStatusViewHolder(itemView: View, viewType: Int, var listener: ClickListener) : RecyclerView.ViewHolder(itemView), View.OnClickListener  {
    var tvTitle: TextView = itemView.findViewById(R.id.tv_timeline_title)
    var tvDate: TextView = itemView.findViewById(R.id.tv_timeline_date)
    private var timelineView = itemView.findViewById<TimelineView>(R.id.timeline)

    override fun onClick(view: View) {
        listener.onClick(view, adapterPosition)
    }

    init {
        timelineView!!.initLine(viewType)
        itemView.setOnClickListener(this)
    }
}