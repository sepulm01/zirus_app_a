package com.iramml.zirusapp.user.model

data class GeoCodingResult (
    var results: ArrayList<Result> = ArrayList()
)

data class Result (
    var formatted_address: String = ""
)
