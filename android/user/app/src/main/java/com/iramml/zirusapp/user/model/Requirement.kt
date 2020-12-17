package com.iramml.zirusapp.user.model

data class Requirement (
    var name: String = "",
    var address: String = "",
    var funa: String = "",
    var location: String = "",
    var description: String = "",
    var image: String = "",
    var statusItems: ArrayList<RequirementStatusItem> = ArrayList(),
    var lat: Double = 0.0,
    var lng: Double = 0.0,
)

data class RequirementStatusItem (
    var name: String,
    var description: String,
    var image: String,
    var date: String,
)