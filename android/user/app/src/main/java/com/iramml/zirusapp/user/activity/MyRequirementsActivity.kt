package com.iramml.zirusapp.user.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
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
    private lateinit var btnShowMore: Button
    private lateinit var requirementsAdapter: RequirementsAdapter
    private lateinit var pbRequirements: ProgressBar

    private var currentPage = 1
    private val requirementsArrayList = ArrayList<Requirement>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_requirements)
        initViews()
        initListeners()
        implementRequirementsList()
        getRequirements()
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
        btnShowMore = findViewById(R.id.btn_show_more)
        pbRequirements = findViewById(R.id.pb_requirements)
    }

    private fun initListeners() {
        btnShowMore.setOnClickListener {
            currentPage++
            getRequirements()
        }
    }

    private fun getRequirements() {
        pbRequirements.visibility = View.VISIBLE
        val requirementFirebaseModel = RequirementFirebaseModel()
        requirementFirebaseModel.getRequirements(currentPage, object : RequirementListener.GetRequirementsListener {
            override fun onSuccess(requirements: ArrayList<Requirement>) {
                pbRequirements.visibility = View.GONE
                if (requirements.size > 0) {
                    requirementsArrayList.addAll(requirements)
                    requirementsAdapter.notifyDataSetChanged()
                    btnShowMore.visibility = View.VISIBLE
                } else {
                    btnShowMore.visibility = View.GONE
                }

            }

        })
    }

    private fun implementRequirementsList() {
        requirementsAdapter = RequirementsAdapter(
                this,
                requirementsArrayList,
                object : ClickListener {
                    override fun onClick(view: View?, index: Int) {
                        val intent = Intent(this@MyRequirementsActivity, RequirementDetailActivity::class.java)
                        intent.putExtra("requirement_id", requirementsArrayList[index].id)
                        startActivity(intent)
                    }
                }
        )
        rvRequirements.adapter = requirementsAdapter
    }

}