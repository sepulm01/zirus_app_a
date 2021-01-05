package com.iramml.zirusapp.user.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.adapter.ClickListener
import com.iramml.zirusapp.user.adapter.categorylist.CategoriesAdapter
import com.iramml.zirusapp.user.model.RequirementFirebaseModel
import com.iramml.zirusapp.user.model.RequirementListener
import com.iramml.zirusapp.user.model.schema.firebase.RequirementCategory

class ChooseRequirementCategoryActivity : AppCompatActivity() {
    private lateinit var rvRequirementCategory: RecyclerView

    private var latSelected: Double = -1.0
    private var lngSelected: Double = -1.0
    private var address: String = ""
    private var categoryName: String = ""
    private var categoryIcon: String = ""
    private var categoryID: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_requirement_category)
        initViews()
        initListeners()
        getRequirementCategories()

        if (intent.extras != null) {
            latSelected = intent.getDoubleExtra("lat", -0.1)
            lngSelected = intent.getDoubleExtra("lng", -0.1)
            address = intent.getStringExtra("address").toString()
        } else
            finish()
    }

    private fun initViews() {
        rvRequirementCategory = findViewById(R.id.rv_requirement_categories)
        rvRequirementCategory.isNestedScrollingEnabled = true
        rvRequirementCategory.layoutManager =
            GridLayoutManager(this, 3,
                RecyclerView.VERTICAL, false)
    }

    private fun initListeners() {

    }

    private fun getRequirementCategories() {
        val requirementFirebaseModel = RequirementFirebaseModel()
        requirementFirebaseModel.getRequirementCategories(object: RequirementListener.GetRequirementCategoriesListener {
            override fun onSuccess(requirementCategories: List<RequirementCategory>) {
                val categoriesAdapter = CategoriesAdapter(
                        this@ChooseRequirementCategoryActivity,
                        requirementCategories,
                        object: ClickListener {
                            override fun onClick(view: View?, index: Int) {
                                goToNewRequirement(requirementCategories[index])
                            }
                        }
                    )
                rvRequirementCategory.adapter = categoriesAdapter
            }

        })
    }

    private fun goToNewRequirement(requirementCategory: RequirementCategory) {
        val intent = Intent(this, NewRequirementActivity::class.java)
        intent.putExtra("lat", latSelected)
        intent.putExtra("lng", lngSelected)
        intent.putExtra("address", address)
        intent.putExtra("category", Gson().toJson(requirementCategory))
        startActivity(intent)
    }
}