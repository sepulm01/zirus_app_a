package com.iramml.zirusapp.user.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.DatabaseError
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.common.Common
import com.iramml.zirusapp.user.firebase.RequirementFirebaseHelper
import com.iramml.zirusapp.user.firebase.RequirementListener
import com.iramml.zirusapp.user.helper.GoogleAPIHelper
import com.iramml.zirusapp.user.helper.GoogleAPIsListener
import com.iramml.zirusapp.user.listener.LocationListener
import com.iramml.zirusapp.user.message.Errors
import com.iramml.zirusapp.user.message.Messages
import com.iramml.zirusapp.user.message.ShowMessage
import com.iramml.zirusapp.user.model.firebase.Requirement
import com.iramml.zirusapp.user.model.firebase.RequirementStatusItem
import com.iramml.zirusapp.user.util.LocationUtil
import com.iramml.zirusapp.user.util.Utilities
import dmax.dialog.SpotsDialog
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class SOSActivity : AppCompatActivity() {
    private lateinit var root: View
    private lateinit var ivBack: ImageView
    private lateinit var btnConfirm: Button

    private lateinit var locationUtil: LocationUtil

    private var currentLocation = LatLng(-1.0, -1.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sos)
        initViews()
        initListeners()
        locationUtil = LocationUtil(this, object: LocationListener {
            override fun locationResponse(response: LocationResult) {
                locationUtil.stopUpdateLocation()
                currentLocation = LatLng(response.lastLocation.latitude, response.lastLocation.latitude)
            }

        })
    }

    private fun initViews() {
        root = findViewById(R.id.root)
        ivBack = findViewById(R.id.iv_back)
        btnConfirm = findViewById(R.id.btn_confirm)
    }

    private fun initListeners() {
        ivBack.setOnClickListener {
            finish()
        }

        btnConfirm.setOnClickListener {
            validateData()
        }
    }

    override fun onStart() {
        super.onStart()
        locationUtil.initializeLocation()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationUtil.onRequestPermissionResult(requestCode, permissions, grantResults)
    }

    private fun validateData() {
        if (currentLocation.latitude != -1.0 &&
            currentLocation.longitude != -1.0) {
            val waitingDialog = SpotsDialog.Builder().setContext(this).build()
            waitingDialog.show()
            val googleAPIHelper = GoogleAPIHelper(this@SOSActivity)
            googleAPIHelper.getAddressByLatLng(
                    currentLocation,
                    object: GoogleAPIsListener.GetAddressByLatLngListener {
                        override fun onRequestResult(address: String) {
                            waitingDialog.dismiss()
                            createSOSRequirement(address)
                        }

                    }
            )
        } else {
            locationUtil.initializeLocation()
            ShowMessage.message(root, this@SOSActivity, Messages.GIVE_LOCATION_PERMISSION)
        }

    }

    private fun createSOSRequirement(address: String) {
        val waitingDialog = SpotsDialog.Builder().setContext(this).build()
        waitingDialog.show()
        val requirement = Requirement()
        requirement.user = Common.currentUser!!
        requirement.lat = currentLocation.latitude
        requirement.lng = currentLocation.longitude
        requirement.type = "sos"
        requirement.requirement_num = "sos" + createRandomNumber(8)
        requirement.details.address = address

        val currentDateTime = getCurrentDateTime()
        requirement.dateTime = currentDateTime
        val requirementStatusItem = RequirementStatusItem(
                "Ingresado",
                "",
                "",
                currentDateTime,
        )
        requirement.statusItems.add(requirementStatusItem)

        val requirementFirebaseHelper = RequirementFirebaseHelper()
        requirementFirebaseHelper.createSOSRequirement(
            requirement,
            object: RequirementListener.CreateSOSRequirementListener {
                override fun onSuccessListener() {
                    waitingDialog.dismiss()
                    startActivity(
                            Intent(
                                    this@SOSActivity,
                                    SOSSentActivity::class.java
                            )
                    )
                    finish()
                }

                override fun onRegisterReferenceDetailsFailure(exception: DatabaseError) {
                    waitingDialog.dismiss()
                    ShowMessage.messageError(root, this@SOSActivity, Errors.FAILED)
                }

                override fun onRegisterReferenceListFailure(exception: Exception) {
                    waitingDialog.dismiss()
                    Utilities.displayMessage(
                            root,
                            this@SOSActivity,
                            exception.localizedMessage
                    )
                }

            }
        )
    }

    @SuppressLint("SimpleDateFormat")
    private fun getCurrentDateTime(): String {
        val date: Date = Calendar.getInstance().time
        val dateFormat: DateFormat = SimpleDateFormat("yyyy-mm-dd hh:mm:ss")
        return dateFormat.format(date)
    }

    private fun createRandomNumber(len: Long): String {
        //check(len <= 18) { "To many digits" }
        val tLen = Math.pow(10.0, (len - 1).toDouble()).toLong() * 9
        val number = (Math.random() * tLen).toLong() + Math.pow(10.0, (len - 1).toDouble()).toLong() * 1
        val tVal = number.toString() + ""
        //check(tVal.length.toLong() == len) { "The random number '$tVal' is not '$len' digits" }
        return tVal
    }
}