package com.iramml.zirusapp.user.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseError
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.common.Common
import com.iramml.zirusapp.user.firebase.AuthFirebaseHelper
import com.iramml.zirusapp.user.message.FormMessages
import com.iramml.zirusapp.user.message.ShowMessage
import com.iramml.zirusapp.user.model.NormalUser
import com.iramml.zirusapp.user.util.Utilities
import dmax.dialog.SpotsDialog
import java.lang.Exception

class SignInActivity : AppCompatActivity() {
    private lateinit var root: View
    private lateinit var tvSignUp: TextView
    private lateinit var etEmail: TextInputEditText
    private lateinit var etPassword: TextInputEditText
    private lateinit var btnContinue: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        initViews()
        initListeners()
    }

    private fun initViews() {
        root = findViewById(R.id.root)
        tvSignUp = findViewById(R.id.tv_sign_up)
        etEmail = findViewById(R.id.et_email)
        etPassword = findViewById(R.id.et_password)
        btnContinue = findViewById(R.id.btn_continue)
    }

    private fun initListeners() {
        btnContinue.setOnClickListener {
            validateSignInData()
        }
        tvSignUp.setOnClickListener {
            startActivity(Intent(this@SignInActivity, SignUpActivity::class.java))
        }
    }

    private fun validateSignInData() {
        Utilities.hideKeyboard(this)
        if (TextUtils.isEmpty(etEmail.text)) {
            ShowMessage.messageForm(root, this, FormMessages.FILL_EMAIL)
            return
        }
        if (TextUtils.isEmpty(etPassword.text)) {
            ShowMessage.messageForm(root, this, FormMessages.FILL_PASSWORD)
            return
        }

        signIn()
    }

    private fun signIn() {
        val waitingDialog = SpotsDialog.Builder().setContext(this).build()
        waitingDialog.show()

        AuthFirebaseHelper().signIn(etEmail.text.toString(), etPassword.text.toString(), object: AuthFirebaseHelper.SignInListener {
            override fun onCompleteListener(user: NormalUser) {
                waitingDialog.dismiss()
                Common.currentUser = user
                startActivity(Intent(this@SignInActivity, HomeActivity::class.java))
                finish()
            }

            override fun onFailureListener(exception: Exception) {
                waitingDialog.dismiss()
                if (exception.localizedMessage != "")
                    displayMessage(exception.localizedMessage!!)
            }

            override fun onCancelledGetCurrentUserListener(exception: DatabaseError) {
                waitingDialog.dismiss()
            }

        })
    }

    fun displayMessage(message: String) {
        Utilities.displayMessage(root, this, message)
    }
}