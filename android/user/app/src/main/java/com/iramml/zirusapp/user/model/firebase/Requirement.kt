package com.iramml.zirusapp.user.model.firebase

data class Requirement (
    var id: String = "",
    var type: String = "",
    var dateTime: String = "",
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var details: RequirementDetails = RequirementDetails(),
    var user: NormalUser = NormalUser(),
    var statusItems: ArrayList<RequirementStatusItem> = ArrayList(),
)


data class RequirementStatusItem (
    var name: String = "",
    var description: String = "",
    var image: String = "",
    var date: String = "",
)

data class RequirementDetails (
    var address: String = "",
    var description: String = "",
    var image: String = "",
)
