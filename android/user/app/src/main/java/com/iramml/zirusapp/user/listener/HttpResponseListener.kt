package com.iramml.zirusapp.user.listener

import com.android.volley.VolleyError

interface HttpResponseListener {
    fun httpResponseSuccess(responseText: String)
    fun httpResponseError(error: VolleyError)
}