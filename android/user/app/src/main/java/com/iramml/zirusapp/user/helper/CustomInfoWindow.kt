package com.iramml.zirusapp.user.helper

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.iramml.zirusapp.user.R


class CustomInfoWindow(context: Context): GoogleMap.InfoWindowAdapter {
    private var root: View

    init {
        root = LayoutInflater.from(context).inflate(R.layout.template_custom_window, null)
    }

    override fun getInfoWindow(marker: Marker): View? {
        val tvPickupTitle: TextView = root.findViewById(R.id.tv_info)
        val tvPickupSnippet: TextView = root.findViewById(R.id.tv_snippet)

        tvPickupTitle.text = marker.title
        tvPickupSnippet.text = marker.snippet
        return root
    }

    override fun getInfoContents(marker: Marker?): View? {
        return null
    }

}