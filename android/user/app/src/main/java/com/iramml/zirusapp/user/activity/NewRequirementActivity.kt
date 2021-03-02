package com.iramml.zirusapp.user.activity

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseError
import com.google.gson.Gson
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.common.Common
import com.iramml.zirusapp.user.helper.GoogleAPIHelper
import com.iramml.zirusapp.user.helper.GoogleAPIsListener
import com.iramml.zirusapp.user.message.Errors
import com.iramml.zirusapp.user.message.FormMessages
import com.iramml.zirusapp.user.message.ShowMessage
import com.iramml.zirusapp.user.model.RequirementFirebaseModel
import com.iramml.zirusapp.user.model.RequirementListener
import com.iramml.zirusapp.user.model.schema.firebase.Requirement
import com.iramml.zirusapp.user.model.schema.firebase.RequirementCategory
import com.iramml.zirusapp.user.model.schema.firebase.RequirementStatusItem
import com.iramml.zirusapp.user.util.LocationUtil
import com.iramml.zirusapp.user.util.Utilities
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import dmax.dialog.SpotsDialog
import java.io.IOException
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class NewRequirementActivity : AppCompatActivity() {
    private lateinit var root: View
    private lateinit var toolbar: Toolbar
    private lateinit var etDescription: TextInputEditText
    private lateinit var ivPhoto: ImageView
    private lateinit var btnCreate: Button

    private var locationUtil: LocationUtil? = null
    private lateinit var googleAPIHelper: GoogleAPIHelper

    private var requirementImgBitmap: Bitmap? = null
    private val PICK_IMAGE_REQUEST = 2002

    private var latSelected: Double = -1.0
    private var lngSelected: Double = -1.0
    private var locationAddress: String = ""
    private var requirementCategory: RequirementCategory = RequirementCategory()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_requirement)
        googleAPIHelper = GoogleAPIHelper(this)
        initViews()
        initListeners()
        if (intent.extras != null) {
            latSelected = intent.getDoubleExtra("lat", -0.1)
            lngSelected = intent.getDoubleExtra("lng", -0.1)

            getAddressByLatLng(LatLng(latSelected, lngSelected))
            requirementCategory = Gson()
                    .fromJson(
                            intent.getStringExtra("category").toString(),
                            RequirementCategory::class.java
                    )
        } else
            finish()
    }

    private fun initViews() {
        root = findViewById(R.id.root)
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.new_requirement)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        ivPhoto = findViewById(R.id.iv_photo)
        etDescription = findViewById(R.id.et_description)
        btnCreate = findViewById(R.id.btn_create)
    }

    private fun initListeners() {
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

        val requirement = Requirement()
        requirement.user = Common.currentUser!!
        requirement.lat = latSelected
        requirement.lng = lngSelected
        requirement.type = "normal"
        requirement.requirement_num = "req" + createRandomNumber(8)
        requirement.details.description = etDescription.text.toString()
        requirement.details.address = locationAddress
        requirement.category = requirementCategory

        val date: Date = Calendar.getInstance().time
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-mm-dd hh:mm:ss")
        val strDate: String = dateFormat.format(date)

        requirement.dateTime = strDate
        requirement.timeZone = getTimeZone()
        requirement.status = "sent"
        val requirementStatusItem = RequirementStatusItem(
                "${etDescription.text.toString()} - Ingresado",
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

        val requirementFirebaseModel = RequirementFirebaseModel()
        requirementFirebaseModel.createNormalRequirement(
                requirement,
                requirementImgBitmap,
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

    private fun getAddressByLatLng(location: LatLng) {
        val googleAPIHelper = GoogleAPIHelper(this)
        googleAPIHelper.getAddressByLatLng(
                location,
                object : GoogleAPIsListener.GetAddressByLatLngListener {
                    override fun onRequestResult(address: String) {
                        locationAddress = address
                    }
                }
        )
    }

    private fun getTimeZone(): String {
        val tz = TimeZone.getDefault()
        val gmt1 = TimeZone.getTimeZone(tz.id)
                .getDisplayName(false, TimeZone.SHORT)
        val gmt2 = TimeZone.getTimeZone(tz.id)
                .getDisplayName(false, TimeZone.LONG)

        return "$gmt1\t$gmt2"
    }
}