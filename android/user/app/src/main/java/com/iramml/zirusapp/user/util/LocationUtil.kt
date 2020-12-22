package com.iramml.zirusapp.user.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.iramml.zirusapp.user.listener.LocationListener
import com.iramml.zirusapp.user.message.Errors
import com.iramml.zirusapp.user.message.Messages
import com.iramml.zirusapp.user.message.ShowMessage


class LocationUtil(var activity: Activity, locationListener: LocationListener) {
    private val permissionFineLocation = Manifest.permission.ACCESS_FINE_LOCATION
    private val permissionCoarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION

    private val REQUEST_CODE_LOCATION = 100

    private var fusedLocationClient: FusedLocationProviderClient

    private lateinit var locationRequest: LocationRequest
    private var locationCallback: LocationCallback? = null

    init {
        fusedLocationClient = FusedLocationProviderClient(activity.applicationContext)
        initializeLocationRequest()
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                locationListener.locationResponse(locationResult)
            }
        }
    }


    private fun initializeLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 3000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun validatePermissionsLocation(): Boolean {
        val fineLocationAvailable = ActivityCompat.checkSelfPermission(activity.applicationContext, permissionFineLocation) == PackageManager.PERMISSION_GRANTED
        val coarseLocationAvailable = ActivityCompat.checkSelfPermission(activity.applicationContext, permissionCoarseLocation) == PackageManager.PERMISSION_GRANTED
        return fineLocationAvailable && coarseLocationAvailable
    }

    private fun permissionRequest() {
        ActivityCompat.requestPermissions(activity, arrayOf(permissionFineLocation, permissionCoarseLocation), REQUEST_CODE_LOCATION)
    }

    private fun requestPermissions() {
        val contextProvider = ActivityCompat.shouldShowRequestPermissionRationale(activity, permissionFineLocation)
        if (contextProvider) ShowMessage.message(null, activity!!.applicationContext, Messages.RATIONALE_LOCATION)
        permissionRequest()
    }

    fun onRequestPermissionResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getLocation()
                else
                    ShowMessage.messageError(null, activity.applicationContext, Errors.LOCATION_PERMISSION_DENIED)
            }
        }
    }

    fun initializeLocation() {
        if (validatePermissionsLocation())
            getLocation()
        else
            requestPermissions()
    }

    fun stopUpdateLocation() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        validatePermissionsLocation()
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

}