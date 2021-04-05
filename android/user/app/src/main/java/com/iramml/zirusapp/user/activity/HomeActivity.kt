package com.iramml.zirusapp.user.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.common.Common
import com.iramml.zirusapp.user.model.AuthFirebaseModel

class HomeActivity : AppCompatActivity() {
    private lateinit var ivLogOut: ImageView
    private lateinit var tvWelcome: TextView
    private lateinit var btnNewRequirement: Button
    private lateinit var btnMyRequirements: Button
    private lateinit var btnSOS: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initViews()
        initListeners()
        getFirebaseToken()
        tvWelcome.text = "${getString(R.string.welcome_comma)} ${Common.currentUser?.firstName}"
    }

    private fun initViews() {
        ivLogOut = findViewById(R.id.iv_log_out)
        tvWelcome = findViewById(R.id.tv_welcome)
        btnNewRequirement = findViewById(R.id.btn_new_requirement)
        btnMyRequirements = findViewById(R.id.btn_my_requirements)
        btnSOS = findViewById(R.id.btn_sos)
    }

    private fun initListeners() {
        ivLogOut.setOnClickListener {
            AuthFirebaseModel.logout()
            startActivity(Intent(this@HomeActivity, SignInActivity::class.java))
            finish()
        }

        btnNewRequirement.setOnClickListener {
            startActivity(Intent(this@HomeActivity, NewLocationActivity::class.java))
        }

        btnMyRequirements.setOnClickListener {
            startActivity(Intent(this@HomeActivity, MyRequirementsActivity::class.java))
        }

        btnSOS.setOnClickListener {
            startActivity(Intent(this@HomeActivity, SOSActivity::class.java))
        }
    }

    private fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                return@OnCompleteListener
            }

            val token = task.result
            val authFirebaseModel: AuthFirebaseModel = AuthFirebaseModel()
            authFirebaseModel.saveFirebaseToken(token)
        })
    }
}