package com.iramml.zirusapp.user.activity

import android.Manifest
import android.R.attr
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
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
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseError
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.adapter.ClickListener
import com.iramml.zirusapp.user.adapter.placeslist.PlacesAdapter
import com.iramml.zirusapp.user.common.Common
import com.iramml.zirusapp.user.firebase.RequirementFirebaseHelper
import com.iramml.zirusapp.user.firebase.RequirementListener
import com.iramml.zirusapp.user.helper.GoogleAPIHelper
import com.iramml.zirusapp.user.helper.GoogleAPIsListener
import com.iramml.zirusapp.user.listener.LocationListener
import com.iramml.zirusapp.user.message.Errors
import com.iramml.zirusapp.user.message.FormMessages
import com.iramml.zirusapp.user.message.ShowMessage
import com.iramml.zirusapp.user.model.firebase.Requirement
import com.iramml.zirusapp.user.model.firebase.RequirementStatusItem
import com.iramml.zirusapp.user.model.googleapis.PlacesResponse
import com.iramml.zirusapp.user.model.googleapis.PlacesResult
import com.iramml.zirusapp.user.util.BitmapUtils
import com.iramml.zirusapp.user.util.LocationUtil
import com.iramml.zirusapp.user.util.Utilities
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dmax.dialog.SpotsDialog
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class NewRequirementActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var root: View
    private lateinit var clSearchLocation: ConstraintLayout
    private lateinit var cvSearchLocation: CardView
    private lateinit var ivBack: ImageView
    private lateinit var etAddress: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var etSearchAddress: EditText
    private lateinit var ivPhoto: ImageView
    private lateinit var btnCurrentLocation: Button
    //private lateinit var btnChooseLocation: Button
    private lateinit var btnCreate: Button
    private lateinit var rvPlaces: RecyclerView

    private lateinit var googleMap: GoogleMap
    private var locationMarker: Marker? = null

    private var locationUtil: LocationUtil? = null
    private lateinit var googleAPIHelper: GoogleAPIHelper

    private val CHOOSE_LOCATION_ACTIVITY = 2001
    private val PICK_IMAGE_REQUEST = 2002
    private var latSelected: Double = -1.0
    private var lngSelected: Double = -1.0
    private var locationChile = LatLng(-21.995569, -69.2150163)
    private var requirementImgBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_requirement)
        googleAPIHelper = GoogleAPIHelper(this)
        initViews()
        initListeners()
    }

    private fun initViews() {
        clSearchLocation = findViewById(R.id.cl_search_location)
        cvSearchLocation = findViewById(R.id.cv_search_location)
        root = findViewById(R.id.root)
        ivBack = findViewById(R.id.iv_back)
        ivPhoto = findViewById(R.id.iv_photo)
        etAddress = findViewById(R.id.et_address)
        etDescription = findViewById(R.id.et_description)
        rvPlaces = findViewById(R.id.rv_places)
        rvPlaces.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        //etAddress.isEnabled = false
        btnCurrentLocation = findViewById(R.id.btn_current_location)
        //btnChooseLocation = findViewById(R.id.btn_choose_location)
        btnCreate = findViewById(R.id.btn_create)
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        etSearchAddress = findViewById(R.id.et_search_address)
    }

    private fun initListeners() {
        ivBack.setOnClickListener {
            finish()
        }

        btnCurrentLocation.setOnClickListener {
            locationUtil = LocationUtil(this, object : LocationListener {
                override fun locationResponse(response: LocationResult) {
                    locationUtil!!.stopUpdateLocation()
                    latSelected = response.lastLocation.latitude
                    lngSelected = response.lastLocation.longitude

                    val location = LatLng(latSelected, lngSelected)
                    putLocationOnMap(location)

                    val googleAPIHelper = GoogleAPIHelper(this@NewRequirementActivity)
                    googleAPIHelper.getAddressByLatLng(
                            LatLng(latSelected, lngSelected),
                            object : GoogleAPIsListener.GetAddressByLatLngListener {
                                override fun onRequestResult(address: String) {
                                    etAddress.setText(address)
                                }

                            }
                    )

                }

            })
            locationUtil!!.initializeLocation()
        }

        ivPhoto.setOnClickListener {
            chooseImage()
        }

//        btnChooseLocation.setOnClickListener {
//            val intent = Intent(this@NewRequirementActivity, NewLocationActivity::class.java)
//            startActivityForResult(intent, CHOOSE_LOCATION_ACTIVITY);
//        }

        btnCreate.setOnClickListener {
            validateRequirementData()
        }

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

                    val location =
                    if (latSelected != -1.0 && lngSelected != -1.0) {
                        LatLng(latSelected, lngSelected)
                    } else
                        locationChile

                    googleAPIHelper.getPlacesByAddressString(
                            queryEncode,
                            location,
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

        etAddress.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                toggleSearchLocation()
            }
        }

        cvSearchLocation.setOnClickListener(View.OnClickListener {
            toggleSearchLocation()
        })
    }

    private fun validateRequirementData() {
        if (TextUtils.isEmpty(etAddress.text.toString())) {
            ShowMessage.messageForm(root, this@NewRequirementActivity, FormMessages.FILL_ADDRESS)
            return
        }

        if (TextUtils.isEmpty(etDescription.text.toString())) {
            ShowMessage.messageForm(
                    root,
                    this@NewRequirementActivity,
                    FormMessages.FILL_DESCRIPTION
            )
            return
        }

        if (requirementImgBitmap == null) {
            ShowMessage.messageForm(root, this@NewRequirementActivity, FormMessages.SELECT_PHOTO)
            return
        }

        val requirement = Requirement()
        requirement.user = Common.currentUser!!
        requirement.lat = latSelected
        requirement.lng = lngSelected
        requirement.type = "normal"
        requirement.requirement_num = "req" + createRandomNumber(8)
        requirement.details.description = etDescription.text.toString()
        requirement.details.address = etAddress.text.toString()

        val date: Date = Calendar.getInstance().time
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-mm-dd hh:mm:ss")
        val strDate: String = dateFormat.format(date)

        requirement.dateTime = strDate
        val requirementStatusItem = RequirementStatusItem(
                "Ingresado",
                "",
                "",
                strDate,
        )
        requirement.statusItems.add(requirementStatusItem)
        createRequirement(requirement)
    }

    private fun createRequirement(requirement: Requirement) {
        val waitingDialog = SpotsDialog.Builder().setContext(this).build()
        waitingDialog.show()
        val requirementFirebaseHelper = RequirementFirebaseHelper()
        requirementFirebaseHelper.createNormalRequirement(
                requirement,
                requirementImgBitmap!!,
                object : RequirementListener.CreateNormalRequirementListener {
                    override fun onSuccessListener() {
                        waitingDialog.dismiss()
                        startActivity(
                                Intent(
                                        this@NewRequirementActivity,
                                        MyRequirementsActivity::class.java
                                )
                        )
                        finish()
                    }

                    override fun onUploadedImageError(exception: Exception) {
                        waitingDialog.dismiss()
                        Utilities.displayMessage(
                                root,
                                this@NewRequirementActivity,
                                exception.localizedMessage
                        )
                    }

                    override fun onRegisterReferenceDetailsFailure(exception: DatabaseError) {
                        waitingDialog.dismiss()
                        ShowMessage.messageError(root, this@NewRequirementActivity, Errors.FAILED)
                    }

                    override fun onRegisterReferenceListFailure(exception: Exception) {
                        waitingDialog.dismiss()
                        Utilities.displayMessage(
                                root,
                                this@NewRequirementActivity,
                                exception.localizedMessage
                        )
                    }

                }
        )
    }

    private fun chooseImage() {
        Dexter.withContext(this)
                .withPermissions(Manifest.permission.CAMERA)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {

//                          val intent = Intent(Intent.ACTION_PICK)
//                          intent.type = "image/*"
//                          intent.action = Intent.ACTION_GET_CONTENT
//                          startActivityForResult(intent, PICK_IMAGE_REQUEST)

                            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            startActivityForResult(cameraIntent, PICK_IMAGE_REQUEST)

                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                            permission: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                            token: PermissionToken?
                    ) {
                        token!!.continuePermissionRequest()
                    }

                }).check()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHOOSE_LOCATION_ACTIVITY) {
            if (resultCode == RESULT_OK) {
                val latResult = data?.getDoubleExtra("lat", -1.0)
                val lngResult = data?.getDoubleExtra("lng", -1.0)
                val address = data?.getStringExtra("address")

                if (latResult != -1.0 && lngResult != -1.0 && address != "") {
                    latSelected = latResult!!
                    lngSelected = lngResult!!
                    etAddress.setText(address)

                    val location = LatLng(latSelected, lngSelected)
                    putLocationOnMap(location)
                }
            }
        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK /*&& data != null && data.data != null*/) {
            //requirementImgUri = data.data

            try {
                //val bitmap = BitmapUtils().getBitmapFromGallery(this, requirementImgUri, 750, 750)
                val bitmap: Bitmap = data!!.extras?.get("data") as Bitmap
                requirementImgBitmap = bitmap
                ivPhoto.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        if (locationUtil != null)
            locationUtil!!.stopUpdateLocation()
    }

    private fun createRandomNumber(len: Long): String {
        val tLen = Math.pow(10.0, (len - 1).toDouble()).toLong() * 9
        val number = (Math.random() * tLen).toLong() + Math.pow(10.0, (len - 1).toDouble()).toLong() * 1
        val tVal = number.toString() + ""
        return tVal
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
    }

    private fun putLocationOnMap(location: LatLng) {
        if (locationMarker != null)
            locationMarker!!.remove()

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15.0f))
        locationMarker = googleMap.addMarker(
                MarkerOptions().position(location)
        )
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
                        putLocationOnMap(locationClicked)
                        etAddress.setText(placesList[index].formatted_address)
                        toggleSearchLocation()
                    }
                }
        )
        rvPlaces.adapter = placesAdapter
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
}