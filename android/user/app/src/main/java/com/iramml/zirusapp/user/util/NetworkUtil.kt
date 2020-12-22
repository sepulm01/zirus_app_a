package com.iramml.zirusapp.user.util

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.android.volley.*
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.iramml.zirusapp.user.listener.HttpResponseListener


class NetworkUtil(var context: Context){
    var queue: RequestQueue? = null

    private fun isAvailableNetwork(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun httpRequest(url: String?, httpResponse: HttpResponseListener) {
        if (isAvailableNetwork()) {
            if (queue == null) queue = Volley.newRequestQueue(context)
            val stringRequest = StringRequest(Request.Method.GET, url,
                { response -> httpResponse.httpResponseSuccess(response) }
            ) { error -> httpResponse.httpResponseError(error) }
            queue!!.add(stringRequest)
        }
    }

    fun httpPOSTRequest(postMap: Map<String, String>, url: String?, httpResponse: HttpResponseListener) {
        if (isAvailableNetwork()) {
            if (queue == null)
                queue = Volley.newRequestQueue(context)
            val stringRequest: StringRequest = object : StringRequest(Method.POST, url,
                Response.Listener { response ->
                    Log.d("SUCCESS_HTTP", response!!)
                    httpResponse.httpResponseSuccess(response)
                },
                Response.ErrorListener { error ->
                    Log.e("HTTP_REQUEST_ERROR", error.toString())
                    httpResponse.httpResponseError(error)
                }) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    return postMap
                }
            }
            stringRequest.retryPolicy = object : RetryPolicy {
                override fun getCurrentTimeout(): Int {
                    return 50000
                }

                override fun getCurrentRetryCount(): Int {
                    return 50000
                }

                @Throws(VolleyError::class)
                override fun retry(error: VolleyError) {
                    httpResponse.httpResponseError(error)
                }
            }
            queue!!.add(stringRequest)
        }
    }
}
