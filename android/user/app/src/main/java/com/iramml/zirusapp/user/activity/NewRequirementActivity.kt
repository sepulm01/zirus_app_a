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


class NewRequirementActivity : AppCompatActivity() {
    private lateinit var root: View
    private lateinit var ivBack: ImageView
    private lateinit var etDescription: TextInputEditText
    private lateinit var ivPhoto: ImageView
    private lateinit var btnCreate: Button

    private var locationMarker: Marker? = null

    private var locationUtil: LocationUtil? = null
    private lateinit var googleAPIHelper: GoogleAPIHelper

    private val PICK_IMAGE_REQUEST = 2002
    private var latSelected: Double = -1.0
    private var lngSelected: Double = -1.0
    private var address: String = ""
    private var locationChile = LatLng(-21.995569, -69.2150163)
    private var requirementImgBitmap: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_requirement)
        googleAPIHelper = GoogleAPIHelper(this)
        initViews()
        initListeners()
        if (intent.extras != null) {
            latSelected = intent.getDoubleExtra("lat", -0.1)
            lngSelected = intent.getDoubleExtra("lng", -0.1)
            address = intent.getStringExtra("address").toString()
        } else
            finish()
    }

    private fun initViews() {
        root = findViewById(R.id.root)
        ivBack = findViewById(R.id.iv_back)
        ivPhoto = findViewById(R.id.iv_photo)
        etDescription = findViewById(R.id.et_description)
        btnCreate = findViewById(R.id.btn_create)
    }

    private fun initListeners() {
        ivBack.setOnClickListener {
            finish()
        }

        ivPhoto.setOnClickListener {
            chooseImage()
        }

        btnCreate.setOnClickListener {
            validateRequirementData()
        }
    }

    private fun validateRequirementData() {
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
        requirement.details.address = address

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
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK ) {
            try {
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

}