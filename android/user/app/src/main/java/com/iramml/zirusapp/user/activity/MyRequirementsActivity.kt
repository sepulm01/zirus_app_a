package com.iramml.zirusapp.user.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.adapter.requirementlist.RequirementsAdapter
import com.iramml.zirusapp.user.model.RequirementFirebaseModel
import com.iramml.zirusapp.user.adapter.ClickListener
import com.iramml.zirusapp.user.model.RequirementListener
import com.iramml.zirusapp.user.model.schema.firebase.Requirement

class MyRequirementsActivity : AppCompatActivity() {
    private lateinit var toolbar: Toolbar
    private lateinit var rvRequirements: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_requirements)
        initViews()
        getData()
    }

    private fun initViews() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.my_requirements)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        rvRequirements = findViewById(R.id.rv_requirements)
        rvRequirements.isNestedScrollingEnabled = true
        rvRequirements.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }

    private fun getData() {
        val requirementFirebaseModel = RequirementFirebaseModel()
        requirementFirebaseModel.getRequirements(object : RequirementListener.GetRequirementsListener {
            override fun onSuccess(requirements: ArrayList<Requirement>) {
                implementRequirementsList(requirements)
            }

        })
    }

    private fun implementRequirementsList(requirements: ArrayList<Requirement>) {
        val requirementsAdapter = RequirementsAdapter(
                this,
                requirements,
                object : ClickListener {
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