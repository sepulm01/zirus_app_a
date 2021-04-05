package com.iramml.zirusapp.user.model

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.iramml.zirusapp.user.common.Common
import com.iramml.zirusapp.user.model.schema.firebase.Requirement
import com.iramml.zirusapp.user.model.schema.firebase.RequirementCategory
import com.iramml.zirusapp.user.util.Utilities
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList


class RequirementFirebaseModel {
    private val requirementUserReference: DatabaseReference
    private val allRequirementsReference: DatabaseReference
    private val requirementCategoryReference: DatabaseReference
    private val firebaseStorage: FirebaseStorage
    private val firebaseAuth: FirebaseAuth

    private val requirements = ArrayList<Requirement>()
    private val PAGINATION_SIZE = 12

    init {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseStorage = FirebaseStorage.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        requirementUserReference = firebaseDatabase.getReference(Common.RequirementUserTable)
        allRequirementsReference = firebaseDatabase.getReference(Common.AllRequirementsTable)
        requirementCategoryReference = firebaseDatabase.getReference(Common.RequirementCategoriesTable)
    }

    fun createNormalRequirement(requirement: Requirement, requirementImg: Bitmap?,
                                createNormalRequirementListener: RequirementListener.CreateNormalRequirementListener) {
        val userId = firebaseAuth.currentUser.uid
        requirement.userId = userId
        if (requirementImg != null) {
            val imageName: String = UUID.randomUUID().toString()
            val imageFolder: StorageReference = firebaseStorage.reference.child("requirement/images/$imageName")

            val stream = ByteArrayOutputStream()
            requirementImg.compress(Bitmap.CompressFormat.PNG, 100, stream)
            val byteArray: ByteArray = stream.toByteArray()
            requirementImg.recycle()
            imageFolder.putBytes(byteArray)
                    .addOnSuccessListener {
                        imageFolder.downloadUrl.addOnSuccessListener { uri ->
                            requirement.details.image = uri.toString()
                            registerNormalRequirement(requirement, createNormalRequirementListener)
                        }
                    }.addOnFailureListener {
                        createNormalRequirementListener.onUploadedImageError(it)
                    }
        } else {
            registerNormalRequirement(requirement, createNormalRequirementListener)
        }
    }

    private fun registerNormalRequirement(requirement: Requirement,
                                          createNormalRequirementListener: RequirementListener.CreateNormalRequirementListener) {
        allRequirementsReference
                .push()
                .setValue(requirement, object : DatabaseReference.CompletionListener {
                    override fun onComplete(error: DatabaseError?, ref: DatabaseReference) {
                        if (error != null) {
                            createNormalRequirementListener.onRegisterReferenceDetailsFailure(error)
                            return
                        }

                        val requirementID = ref.key
                        requirementUserReference
                                .child(firebaseAuth.currentUser!!.uid)
                                .push()
                                .setValue(requirementID)
                                .addOnSuccessListener {
                                    createNormalRequirementListener.onSuccessListener()
                                }.addOnFailureListener {
                                    createNormalRequirementListener.onRegisterReferenceListFailure(it)
                                }
                    }

                })
    }

    fun createSOSRequirement(requirement: Requirement,
                             createNormalRequirementListener: RequirementListener.CreateSOSRequirementListener) {
        val userId = firebaseAuth.currentUser.uid;
        requirement.userId = userId
        allRequirementsReference
                .push()
                .setValue(requirement, object : DatabaseReference.CompletionListener {
                    override fun onComplete(error: DatabaseError?, ref: DatabaseReference) {
                        if (error != null) {
                            createNormalRequirementListener.onRegisterReferenceDetailsFailure(error)
                            return
                        }

                        val requirementID = ref.key
                        requirementUserReference
                                .child(firebaseAuth.currentUser!!.uid)
                                .push()
                                .setValue(requirementID)
                                .addOnSuccessListener {
                                    createNormalRequirementListener.onSuccessListener()
                                }.addOnFailureListener {
                                    createNormalRequirementListener.onRegisterReferenceListFailure(it)
                                }
                    }

                })
    }

    fun getRequirements(currentPage: Int, getRequirementsListener: RequirementListener.GetRequirementsListener) {
        val userId = firebaseAuth.currentUser.uid
        if (requirements.size == 0) {
            allRequirementsReference
                    .orderByChild("userId")
                    .equalTo(userId)
                    .addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            allRequirementsReference.removeEventListener(this)
                            snapshot.children.forEach { child ->
                                val requirement = child.getValue(Requirement::class.java)
                                if (requirement != null) {
                                    requirement.id = child.key!!
                                    requirements.add(requirement)
                                }
                            }
                            requirements.sortBy { requirementItem ->
                                requirementItem.id
                            }
                            requirements.reverse()

                            getRequirementsListener.onSuccess(getRequirementsByPagination(currentPage))
                        }

                        override fun onCancelled(error: DatabaseError) {

                        }
                    })
        } else {
            getRequirementsListener.onSuccess(getRequirementsByPagination(currentPage))
        }
    }

    private fun getRequirementsByPagination(currentPage: Int): ArrayList<Requirement> {
        return ArrayList<Requirement>(Utilities.getPageOfList(requirements, currentPage, PAGINATION_SIZE))
    }

    fun getRequirementByID(requirementID: String, getRequirementListener: RequirementListener.GetRequirementListener) {
        allRequirementsReference
                .child(requirementID)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val requirement = snapshot.getValue(Requirement::class.java)

                        if (requirement != null) {
                            getRequirementListener.onSuccess(requirement)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
    }

    fun getRequirementCategories(getRequirementsListener: RequirementListener.GetRequirementCategoriesListener) {
        val requirementCategories: ArrayList<RequirementCategory> = ArrayList()
        requirementCategoryReference
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { categoryDataSnapshot ->
                            val requirement = categoryDataSnapshot.getValue(RequirementCategory::class.java)

                            if (requirement != null) {
                                requirement.id = categoryDataSnapshot.key!!
                                requirementCategories.add(requirement)
                            }
                        }

                        requirementCategoryReference.removeEventListener(this)
                        getRequirementsListener.onSuccess(requirementCategories)
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }
                })
    }

}