package com.iramml.zirusapp.user.helper

import com.iramml.zirusapp.user.model.googleapis.PlacesResponse

interface GoogleAPIsListener {
    interface GetAddressByLatLngListener {
        fun onRequestResult(address: String)
    }

    interface GetPlacesByStrQueryListener {
        fun onRequestResult(places: PlacesResponse)
    }
}