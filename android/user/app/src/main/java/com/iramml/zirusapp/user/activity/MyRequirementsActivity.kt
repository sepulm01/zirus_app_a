package com.iramml.zirusapp.user.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.iramml.zirusapp.user.R

class MyRequirementsActivity : AppCompatActivity() {
    private lateinit var ivBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_requirements)
        initViews()
        initListeners()
    }

    private fun initViews() {
        ivBack = findViewById(R.id.iv_back)
    }

    private fun initListeners() {
        ivBack.setOnClickListener {
            finish()
        }
    }
}