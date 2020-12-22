package com.iramml.zirusapp.user.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.iramml.zirusapp.user.R

class SOSSentActivity : AppCompatActivity() {
    private lateinit var btnContinue: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sos_sent)
        initViews()
        initListeners()
    }

    private fun initViews() {
        btnContinue = findViewById(R.id.btn_continue)
    }

    private fun initListeners() {
        btnContinue.setOnClickListener {
            startActivity(
                Intent(this@SOSSentActivity, HomeActivity::class.java)
            )
            finish()
        }
    }
}