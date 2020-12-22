package com.iramml.zirusapp.user.activity

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseError
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.common.Common
import com.iramml.zirusapp.user.helper.GoogleAPIHelper
import com.iramml.zirusapp.user.firebase.RequirementFirebaseHelper
import com.iramml.zirusapp.user.firebase.RequirementListener
import com.iramml.zirusapp.user.listener.LocationListener
import com.iramml.zirusapp.user.message.Errors
import com.iramml.zirusapp.user.message.FormMessages
import com.iramml.zirusapp.user.message.ShowMessage
import com.iramml.zirusapp.user.model.Requirement
import com.iramml.zirusapp.user.model.RequirementStatusItem
import com.iramml.zirusapp.user.util.BitmapUtils
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
    private lateinit var ivBack: ImageView
    private lateinit var etAddress: TextInputEditText
    private lateinit var etDescription: TextInputEditText
    private lateinit var ivPhoto: ImageView
    private lateinit var btnCurrentLocation: Button
    private lateinit var btnChooseLocation: Button
    private lateinit var btnCreate: Button

    private var locationUtil: LocationUtil? = null

    private val CHOOSE_LOCATION_ACTIVITY = 2001
    private val PICK_IMAGE_REQUEST = 2002
    private var latSelected: Double = -1.0
    private var lngSelected: Double = -1.0
    private var requirementImgUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_requirement)
        initViews()
        initListeners()
    }

    private fun initViews() {
        root = findViewById(R.id.root)
        ivBack = findViewById(R.id.iv_back)
        ivPhoto = findViewById(R.id.iv_photo)
        etAddress = findViewById(R.id.et_address)
        etDescription = findViewById(R.id.et_description)
        etAddress.isEnabled = false
        btnCurrentLocation = findViewById(R.id.btn_current_location)
        btnChooseLocation = findViewById(R.id.btn_choose_location)
        btnCreate = findViewById(R.id.btn_create)
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

                    val googleAPIHelper = GoogleAPIHelper(this@NewRequirementActivity)
                    googleAPIHelper.getAddressByLatLng(
                        LatLng(latSelected, lngSelected),
                        object : GoogleAPIHelper.GetAddressByLatLngListener {
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

        btnChooseLocation.setOnClickListener {
            val intent = Intent(this@NewRequirementActivity, NewLocationActivity::class.java)
            startActivityForResult(intent, CHOOSE_LOCATION_ACTIVITY);
        }

        btnCreate.setOnClickListener {
            validateRequirementData()
        }
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

        if (requirementImgUri != null && TextUtils.isEmpty(requirementImgUri.toString())) {
            ShowMessage.messageForm(root, this@NewRequirementActivity, FormMessages.SELECT_PHOTO)
            return
        }

        val requirement = Requirement()
        requirement.user = Common.currentUser!!
        requirement.lat = latSelected
        requirement.lng = lngSelected
        requirement.type = "normal"
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
            requirementImgUri!!,
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
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                        if (report.areAllPermissionsGranted()) {
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.type = "image/*"
                            intent.action = Intent.ACTION_GET_CONTENT
                            startActivityForResult(intent, PICK_IMAGE_REQUEST)
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

                if (latResult != -1.0 && lngResult != -1.0) {
                    latSelected = latResult!!
                    lngSelected = lngResult!!
                }
            }
        }
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            requirementImgUri = data.data
            try {
                val bitmap = BitmapUtils().getBitmapFromGallery(this, requirementImgUri, 750, 750)
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


}