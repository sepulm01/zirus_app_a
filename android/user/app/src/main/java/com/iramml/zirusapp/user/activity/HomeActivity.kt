package com.iramml.zirusapp.user.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.helper.AuthFirebaseHelper

class HomeActivity : AppCompatActivity() {
    private lateinit var ivLogOut: ImageView
    private lateinit var btnNewRequirement: Button
    private lateinit var btnMyRequierements: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        initViews()
        initListeners()
    }

    private fun initViews() {
        ivLogOut = findViewById(R.id.iv_log_out)
        btnNewRequirement = findViewById(R.id.btn_new_requirement)
        btnMyRequierements = findViewById(R.id.btn_my_requirements)
    }

    private fun initListeners() {
        ivLogOut.setOnClickListener {
            AuthFirebaseHelper.logout()
            startActivity(Intent(this@HomeActivity, SignInActivity::class.java))
            finish()
        }

        btnNewRequirement.setOnClickListener {
            startActivity(Intent(this@HomeActivity, NewRequirementActivity::class.java))
        }

        btnMyRequierements.setOnClickListener {
            startActivity(Intent(this@HomeActivity, MyRequirementsActivity::class.java))
        }
    }
}