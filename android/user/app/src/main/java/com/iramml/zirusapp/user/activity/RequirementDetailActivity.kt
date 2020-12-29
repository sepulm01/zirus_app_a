package com.iramml.zirusapp.user.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.adapter.ClickListener
import com.iramml.zirusapp.user.adapter.requiremetstatuslist.RequirementStatusAdapter
import com.iramml.zirusapp.user.firebase.RequirementFirebaseHelper
import com.iramml.zirusapp.user.firebase.RequirementListener
import com.iramml.zirusapp.user.model.firebase.Requirement
import com.iramml.zirusapp.user.model.firebase.RequirementStatusItem

class RequirementDetailActivity : AppCompatActivity() {
    private lateinit var ivBack: ImageView
    private lateinit var tvRequirementNumber: TextView
    private lateinit var rvRequirementDetail: RecyclerView

    private lateinit var requirementID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_requirement_detail)
        initViews()
        initListeners()
        if (intent.extras != null) {
            requirementID = intent.getStringExtra("requirement_id")!!
        } else
            finish()
        getRequirementDetails()
    }

    private fun initViews() {
        ivBack = findViewById(R.id.iv_back)
        tvRequirementNumber = findViewById(R.id.tv_requirement_number)
        rvRequirementDetail = findViewById(R.id.rv_requirement_status)
        rvRequirementDetail.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvRequirementDetail.isNestedScrollingEnabled = true
    }

    private fun initListeners() {
        ivBack.setOnClickListener {
            finish()
        }
    }

    private fun getRequirementDetails() {
        val requirementFirebaseHelper = RequirementFirebaseHelper()
        requirementFirebaseHelper.getRequirementByID(requirementID, object: RequirementListener.GetRequirementListener {
            override fun onSuccess(requirementDetails: Requirement) {
                implementRequirementStatus(requirementDetails.statusItems)
                tvRequirementNumber.text = requirementDetails.requirement_num
            }

        })

    }

    private fun implementRequirementStatus(statusItems: ArrayList<RequirementStatusItem>) {
        val requirementStatusAdapter = RequirementStatusAdapter(
            this,
            statusItems,
            object: ClickListener {
                override fun onClick(view: View?, index: Int) {

                }
            }
        )

        rvRequirementDetail.adapter = requirementStatusAdapter
    }
}