package com.iramml.zirusapp.user.helper

import android.content.Context
import android.util.Log
import com.android.volley.VolleyError
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.iramml.zirusapp.user.common.AppConfig
import com.iramml.zirusapp.user.listener.HttpResponseListener
import com.iramml.zirusapp.user.model.GeoCodingResult
import com.iramml.zirusapp.user.model.schema.googleapis.PlacesResponse
import com.iramml.zirusapp.user.util.NetworkUtil


class GoogleAPIHelper(var context: Context) {

    fun getAddressByLatLng(location: LatLng, getAddressByLatLngListener: GoogleAPIsListener.GetAddressByLatLngListener) {
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

    fun getPlacesByAddressString(strAddress: String, location: LatLng, getAddressByLatLngListener: GoogleAPIsListener.GetPlacesByStrQueryListener) {
        val URL_BASE_API_PLACES = "https://maps.googleapis.com/maps/api/place/textsearch/json?";
        val networkUtil: NetworkUtil = NetworkUtil(context)
        val query = "&query=$strAddress"
        val locationStrQuery = "&location=" + location.latitude + "," + location.longitude
        val radius = "radius=1500"
        val key = "&key=" + AppConfig.GOOGLE_API_KEY
        val url: String = (URL_BASE_API_PLACES + radius + locationStrQuery + query + key).replace(" ".toRegex(), "%20")

        networkUtil.httpRequest(
            url,
            object: HttpResponseListener {
                override fun httpResponseSuccess(responseText: String) {
                    val gson = Gson()
                    val placesResponse: PlacesResponse = gson.fromJson(responseText, PlacesResponse::class.java)
                    for (result in placesResponse.results) {
                        if (result.geometry.location.lat == "" || result.geometry.location.lat == "0.0") {
                            placesResponse.results.remove(result)
                        } else if (result.geometry.location.lng == "" || result.geometry.location.lng == "0.0") {
                            placesResponse.results.remove(result)
                        }
                    }
                    getAddressByLatLngListener.onRequestResult(placesResponse)
                }

                override fun httpResponseError(error: VolleyError) {

                }

            }
        )


    }



}