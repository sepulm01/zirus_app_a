package com.iramml.zirusapp.user.model.googleapis

class PlacesResponse(
    var results: ArrayList<PlacesResult> = ArrayList()
)

data class PlacesResult (
    var formatted_address: String = "",
    var geometry: Geometry = Geometry(),
    var iconPlace: Int = -1,
)

data class Geometry(
    var location: Location = Location(),
)

data class Location(
    var lat: String = "",
    var lng: String = "",
)



