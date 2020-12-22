package com.iramml.zirusapp.user.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.adapter.requirementlist.RequirementsAdapter
import com.iramml.zirusapp.user.firebase.RequirementFirebaseHelper
import com.iramml.zirusapp.user.adapter.ClickListener
import com.iramml.zirusapp.user.firebase.RequirementListener
import com.iramml.zirusapp.user.model.Requirement

class MyRequirementsActivity : AppCompatActivity() {
    private lateinit var ivBack: ImageView
    private lateinit var rvRequirements: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_requirements)
        initViews()
        initListeners()
        getData()
    }

    private fun initViews() {
        ivBack = findViewById(R.id.iv_back)
        rvRequirements = findViewById(R.id.rv_requirements)
        rvRequirements.isNestedScrollingEnabled = true
        rvRequirements.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun initListeners() {
        ivBack.setOnClickListener {
            finish()
        }
    }

    private fun getData() {
        val requirementFirebaseHelper = RequirementFirebaseHelper()
        requirementFirebaseHelper.getRequirements(object: RequirementListener.GetRequirementsListener {
            override fun onSuccess(requirements: ArrayList<Requirement>) {
                implementRequirementsList(requirements)
            }

        })
    }

    private fun implementRequirementsList(requirements: ArrayList<Requirement>) {
        val requirementsAdapter = RequirementsAdapter(
            this,
            requirements,
            object: ClickListener {
                override fun onClick(view: View?, index: Int) {
                    val intent = Intent(this@MyRequirementsActivity, RequirementDetailActivity::class.java)
                    intent.putExtra("requirement_id", requirements[index].id)
                    startActivity(intent)
                }
            }
        )

        rvRequirements.adapter = requirementsAdapter
    }
}