package com.iramml.zirusapp.user.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.adapter.ClickListener
import com.iramml.zirusapp.user.adapter.placeslist.PlacesAdapter
import com.iramml.zirusapp.user.helper.CustomInfoWindow
import com.iramml.zirusapp.user.helper.GoogleAPIHelper
import com.iramml.zirusapp.user.helper.GoogleAPIsListener
import com.iramml.zirusapp.user.listener.LocationListener
import com.iramml.zirusapp.user.model.schema.googleapis.PlacesResponse
import com.iramml.zirusapp.user.model.schema.googleapis.PlacesResult
import com.iramml.zirusapp.user.util.LocationUtil
import java.io.UnsupportedEncodingException
import java.net.URLEncoder


class NewLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var llLocationMarker: LinearLayout
    private lateinit var clSearchLocation: ConstraintLayout
    private lateinit var cvSearchLocation: CardView
    private lateinit var cvBack: CardView
    private lateinit var rvPlaces: RecyclerView
    private lateinit var tvAddress: TextView
    private lateinit var etSearchAddress: EditText
    private lateinit var btnConfirm: Button
    private lateinit var googleMap: GoogleMap
    private var location: LocationUtil? = null

    private var locationMarker: Marker? = null
    private lateinit var googleAPIHelper: GoogleAPIHelper

    private var locationSelected = LatLng(-1.0, -1.0)
    private var locationStr: String = ""
    private var isFirstCurrentLocation: Boolean = true
    private var isFirstLoad: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_location)
        googleAPIHelper = GoogleAPIHelper(this)
        initViews()
        initListeners()
    }

    private fun initViews() {
        llLocationMarker = findViewById(R.id.ll_location_marker)
        clSearchLocation = findViewById(R.id.cl_search_location)
        cvSearchLocation = findViewById(R.id.cv_search_location)
        cvBack = findViewById(R.id.cv_back)
        tvAddress = findViewById(R.id.tv_address)
        rvPlaces = findViewById(R.id.rv_places)
        rvPlaces.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        etSearchAddress = findViewById(R.id.et_search_address)
        btnConfirm = findViewById(R.id.btn_confirm)
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun initListeners() {
        cvBack.setOnClickListener {
            finish()
        }

        btnConfirm.setOnClickListener {
            if (locationSelected.latitude != -1.0 && locationSelected.longitude != -1.0) {
                val intent = Intent(this, ChooseRequirementCategoryActivity::class.java)
                intent.putExtra("lat", locationSelected.latitude)
                intent.putExtra("lng", locationSelected.longitude)
                intent.putExtra("address", locationStr)
                startActivity(intent)
            }

        }

        tvAddress.setOnClickListener {
            toggleSearchLocation()
        }

        cvSearchLocation.setOnClickListener(View.OnClickListener {
            toggleSearchLocation()
        })


        location = LocationUtil(this, object : LocationListener {
            @SuppressLint("MissingPermission")
            override fun locationResponse(response: LocationResult) {
                val location = LatLng(
                        response.lastLocation.latitude,
                        response.lastLocation.longitude
                )
                getAddressByLatLng(location)

                if (isFirstCurrentLocation) {
                    btnConfirm.isEnabled = true
                    isFirstCurrentLocation = false
                    locationSelected = location
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f))
                    //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f))
                    locationMarker = googleMap.addMarker(
                            MarkerOptions()
                                    .position(location)
                                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
                    )
                }
                googleMap.isMyLocationEnabled = true
            }

        })

        etSearchAddress.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    var queryEncode = s.toString()
                    try {
                        queryEncode = URLEncoder.encode(s.toString(), "UTF-8")
                    } catch (e: UnsupportedEncodingException) {
                        e.printStackTrace()
                    }
                    googleAPIHelper.getPlacesByAddressString(
                            queryEncode,
                            locationSelected,
                            object : GoogleAPIsListener.GetPlacesByStrQueryListener {
                                override fun onRequestResult(places: PlacesResponse) {
                                    implementRecyclerView(places.results)
                                }

                            }
                    )
                } else {
                    implementRecyclerView(ArrayList())
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })
    }

    override fun onStart() {
        super.onStart()
        location!!.initializeLocation()
    }

    override fun onStop() {
        super.onStop()
        location!!.stopUpdateLocation()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        location!!.onRequestPermissionResult(requestCode, permissions, grantResults)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        this.googleMap.uiSettings.isZoomControlsEnabled = true
        this.googleMap.uiSettings.isZoomGesturesEnabled = false
        this.googleMap.setInfoWindowAdapter(CustomInfoWindow(this))


        this.googleMap.setOnMapClickListener {
            setLocation(this@NewLocationActivity.googleMap.cameraPosition.target)
        }

        this.googleMap.setOnCameraIdleListener {
            if (llLocationMarker.visibility == View.VISIBLE) {
                llLocationMarker.visibility = View.GONE

                if (!isFirstLoad) {
                    setLocation(this.googleMap.cameraPosition.target)
                } else {
                    isFirstLoad = false
                }
            }
        }

        this.googleMap.setOnCameraMoveListener {
            if (locationMarker != null)
                locationMarker?.remove()

            if (llLocationMarker.visibility == View.GONE)
                llLocationMarker.visibility = View.VISIBLE
        }
    }

    private fun toggleSearchLocation() {
        if (clSearchLocation.visibility == View.VISIBLE) {
            clSearchLocation.visibility = View.GONE
            etSearchAddress.clearFocus()
        } else {
            clSearchLocation.visibility = View.VISIBLE
            etSearchAddress.requestFocus()
            etSearchAddress.setText("")
        }
    }

    private fun implementRecyclerView(placesList: ArrayList<PlacesResult>) {
        val placesAdapter = PlacesAdapter(
                this,
                placesList,
                object : ClickListener {
                    override fun onClick(view: View?, index: Int) {
                        val lat = placesList[index].geometry.location.lat.toDouble()
                        val lng = placesList[index].geometry.location.lng.toDouble()

                        val locationClicked = LatLng(lat, lng)
                        setLocation(locationClicked)
                        tvAddress.text = placesList[index].formatted_address
                        toggleSearchLocation()
                    }
                }
        )
        rvPlaces.adapter = placesAdapter
    }

    private fun getAddressByLatLng(location: LatLng) {
        val googleAPIHelper = GoogleAPIHelper(this@NewLocationActivity)
        googleAPIHelper.getAddressByLatLng(
                location,
                object : GoogleAPIsListener.GetAddressByLatLngListener {
                    override fun onRequestResult(address: String) {
                        locationStr = address
                        tvAddress.text = address
                    }
                }
        )
    }

    private fun setLocation(location: LatLng) {
        btnConfirm.isEnabled = true

        if (locationMarker != null)
            locationMarker!!.remove()

        locationSelected = location
        getAddressByLatLng(locationSelected)
        locationMarker = googleMap.addMarker(
                MarkerOptions()
                        .position(location)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.pin))
        )
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f))
    }
}