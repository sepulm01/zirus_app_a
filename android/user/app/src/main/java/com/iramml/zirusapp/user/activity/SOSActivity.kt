package com.iramml.zirusapp.user.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import com.iramml.zirusapp.user.R

class SOSActivity : AppCompatActivity() {
    private lateinit var ivBack: ImageView
    private lateinit var btnConfirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sos)
        initViews()
        initListeners()
    }

    private fun initViews() {
        ivBack = findViewById(R.id.iv_back)
        btnConfirm = findViewById(R.id.btn_confirm)
    }

    private fun initListeners() {
        ivBack.setOnClickListener {
            finish()
        }

        btnConfirm.setOnClickListener {

        }
    }
}