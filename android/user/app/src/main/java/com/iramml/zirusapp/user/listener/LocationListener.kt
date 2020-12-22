package com.iramml.zirusapp.user.listener

import com.google.android.gms.location.LocationResult

interface LocationListener {
    fun locationResponse(response: LocationResult)
}