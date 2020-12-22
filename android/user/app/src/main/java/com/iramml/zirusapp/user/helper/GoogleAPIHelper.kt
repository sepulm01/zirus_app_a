package com.iramml.zirusapp.user.helper

import android.content.Context
import android.util.Log
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpResponse
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.iramml.zirusapp.user.common.AppConfig
import com.iramml.zirusapp.user.listener.HttpResponseListener
import com.iramml.zirusapp.user.model.GeoCodingResult
import com.iramml.zirusapp.user.util.NetworkUtil


class GoogleAPIHelper(var context: Context) {

    fun getAddressByLatLng(location: LatLng, getAddressByLatLngListener: GetAddressByLatLngListener) {
        val networkUtil: NetworkUtil = NetworkUtil(context)
        val url_base = "https://maps.googleapis.com/maps/api/geocode/json?"
        val parameters =
            "latlng=" + location.latitude + "," + location.longitude + "&key=" + AppConfig.GOOGLE_API_KEY
        val url = url_base + parameters
        Log.d("URL_GEOCODE_LOCATION", url)
        networkUtil.httpRequest(url, object : HttpResponseListener {

            override fun httpResponseSuccess(responseText: String) {
                Log.d("PLACE_LOCATION_RES", responseText)
                val gson = Gson()
                val responseObject: GeoCodingResult = gson.fromJson(responseText, GeoCodingResult::class.java)
                var address = ""

                if (responseObject.results[0].formatted_address != "") {
                    address = responseObject.results[0].formatted_address
                    getAddressByLatLngListener.onRequestResult(address)
                }


            }

            override fun httpResponseError(error: VolleyError) {
                TODO("Not yet implemented")
            }
        })
    }


    interface GetAddressByLatLngListener {
        fun onRequestResult(address: String)
    }
}