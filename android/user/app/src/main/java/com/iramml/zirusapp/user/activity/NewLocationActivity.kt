package com.iramml.zirusapp.user.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.helper.CustomInfoWindow
import com.iramml.zirusapp.user.listener.LocationListener
import com.iramml.zirusapp.user.util.LocationUtil


class NewLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var cvBack: CardView
    private lateinit var btnConfirm: Button
    private lateinit var mMap: GoogleMap
    private var location: LocationUtil? = null

    private var locationMarker: Marker? = null

    private var locationSelected = LatLng(-1.0, -1.0)
    private var locationStr: String = ""
    private var isFirstCurrentLocation: Boolean = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_location)
        initViews()
        initListeners()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isZoomGesturesEnabled = true
        mMap.setInfoWindowAdapter(CustomInfoWindow(this))


        mMap.setOnMapClickListener {
            btnConfirm.isEnabled = true
            if (locationMarker != null)
                locationMarker!!.remove()

            locationSelected = mMap.cameraPosition.target
            locationMarker = mMap.addMarker(
                MarkerOptions().position(mMap.cameraPosition.target)
            )
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mMap.cameraPosition.target, 15.0f))
        }

    }

    override fun onStart() {
        super.onStart()
        location!!.initializeLocation()
    }

    override fun onStop() {
        super.onStop()
        location!!.stopUpdateLocation()
    }


    private fun initViews() {
        cvBack = findViewById(R.id.cv_back)
        btnConfirm = findViewById(R.id.btn_confirm)
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initListeners() {
        cvBack.setOnClickListener {
            val returnIntent = Intent()
            setResult(RESULT_CANCELED, returnIntent)
            finish()
        }

        btnConfirm.setOnClickListener {
            if (locationSelected.latitude != -1.0 && locationSelected.longitude != -1.0) {
                val returnIntent = Intent()
                returnIntent.putExtra("lat", locationSelected.latitude)
                returnIntent.putExtra("lng", locationSelected.longitude)
                returnIntent.putExtra("location_name", locationStr)
                setResult(RESULT_OK, returnIntent)
                finish()
            }

        }

        location = LocationUtil(this, object : LocationListener {
            @SuppressLint("MissingPermission")
            override fun locationResponse(response: LocationResult) {
                val location = LatLng(
                    response.lastLocation.latitude,
                    response.lastLocation.longitude
                )

                if (isFirstCurrentLocation){
                    isFirstCurrentLocation = false
                    locationSelected = location
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f))
                    locationMarker = mMap.addMarker(
                        MarkerOptions().position(location)
                    )
                }
                mMap.isMyLocationEnabled = true
            }
        })
    }
}