package com.iramml.zirusapp.user.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import androidx.cardview.widget.CardView
import com.google.android.material.textfield.TextInputEditText
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.common.Common
import com.iramml.zirusapp.user.firebase.AuthFirebaseHelper
import com.iramml.zirusapp.user.message.FormMessages
import com.iramml.zirusapp.user.message.ShowMessage
import com.iramml.zirusapp.user.model.NormalUser
import com.iramml.zirusapp.user.util.Utilities
import dmax.dialog.SpotsDialog
import java.lang.Exception

class SignUpActivity : AppCompatActivity() {
    private lateinit var root: View
    private lateinit var cvBack: CardView
    private lateinit var etFirstName: TextInputEditText
    private lateinit var etLastName: TextInputEditText
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var etPhone: TextInputEditText
    private lateinit var etRUT: TextInputEditText
    private lateinit var btnContinue: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        initViews()
        initListeners()
    }

    private fun initViews() {
        root = findViewById(R.id.root)
        cvBack = findViewById(R.id.cv_back)
        etFirstName = findViewById(R.id.et_first_name)
        etLastName = findViewById(R.id.et_last_name)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        etPhone = findViewById(R.id.et_phone)
        etRUT = findViewById(R.id.et_rut)
        btnContinue = findViewById(R.id.btn_continue)
    }

    private fun initListeners() {
        cvBack.setOnClickListener {
            finish()
        }

        btnContinue.setOnClickListener {
            validateSignUpData()
        }
    }

    private fun validateSignUpData() {
        Utilities.hideKeyboard(this)
        if (TextUtils.isEmpty(etFirstName.text)) {
            ShowMessage.messageForm(root, this, FormMessages.FILL_FIRST_NAME)
            return
        }
        if (TextUtils.isEmpty(etLastName.text)) {
            ShowMessage.messageForm(root, this, FormMessages.FILL_LAST_NAME)
            return
        }
        if (TextUtils.isEmpty(etEmail.text)) {
            ShowMessage.messageForm(root, this, FormMessages.FILL_EMAIL)
            return
        }
        if (TextUtils.isEmpty(etPassword.text)) {
            ShowMessage.messageForm(root, this, FormMessages.FILL_PASSWORD)
            return
        }
        if (TextUtils.isEmpty(etPhone.text)) {
            ShowMessage.messageForm(root, this, FormMessages.FILL_PHONE)
            return
        }
        if (TextUtils.isEmpty(etRUT.text)) {
            ShowMessage.messageForm(root, this, FormMessages.FILL_RUT)
            return
        }

        signUp()
    }

    private fun signUp() {
        val authFirebaseHelper = AuthFirebaseHelper()
        val normalUser = NormalUser(
                etFirstName.text.toString(),
                etLastName.text.toString(),
                etEmail.text.toString(),
                etPhone.text.toString(),
                etRUT.text.toString(),
        )

        val waitingDialog = SpotsDialog.Builder().setContext(this).build()
        waitingDialog.show()

        authFirebaseHelper.signUp(normalUser, etPassword.text.toString(), object: AuthFirebaseHelper.SignUpListener {
            override fun onCompleteListener() {
                waitingDialog.dismiss()
                Common.currentUser = normalUser
                startActivity(Intent(this@SignUpActivity, HomeActivity::class.java))
                finish()
            }

            override fun onCreateUserFailureListener(exception: Exception) {
                waitingDialog.dismiss()
                if (exception.localizedMessage != null)
                    displayMessage(exception.localizedMessage!!)
            }

            override fun onInsertUserDataFailureListener(exception: Exception) {
                waitingDialog.dismiss()
                if (exception.localizedMessage != null)
                    displayMessage(exception.localizedMessage!!)
            }

        })
    }

    fun displayMessage(message: String) {
        Utilities.displayMessage(root, this, message)
    }
}