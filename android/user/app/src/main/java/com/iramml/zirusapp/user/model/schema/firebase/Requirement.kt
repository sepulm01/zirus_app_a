package com.iramml.zirusapp.user.model.schema.firebase

data class Requirement (
    var id: String = "",
    var type: String = "",
    var dateTime: String = "",
    var timeZone: String = "",
    var status: String? = "sent",
    var requirement_num: String = "",
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var details: RequirementDetails = RequirementDetails(),
    var category: RequirementCategory = RequirementCategory(),
    var user: NormalUser = NormalUser(),
    var statusItems: ArrayList<RequirementStatusItem> = ArrayList(),
    var userId: String = "",
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
