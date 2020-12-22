package com.iramml.zirusapp.user.firebase

import com.google.firebase.database.DatabaseError
import com.iramml.zirusapp.user.model.firebase.Requirement
import java.lang.Exception

interface RequirementListener {
    interface CreateNormalRequirementListener {
        fun onSuccessListener()
        fun onUploadedImageError(exception: Exception)
        fun onRegisterReferenceDetailsFailure(exception: DatabaseError)
        fun onRegisterReferenceListFailure(exception: Exception)
    }
    interface CreateSOSRequirementListener {
        fun onSuccessListener()
        fun onRegisterReferenceDetailsFailure(exception: DatabaseError)
        fun onRegisterReferenceListFailure(exception: Exception)
    }

    interface GetRequirementsListener {
        fun onSuccess(requirements: ArrayList<Requirement>)
    }

    interface GetRequirementsIDListener {
        fun onSuccess(requirementsID: ArrayList<String>)
    }

    interface GetRequirementListener {
        fun onSuccess(requirementDetails: Requirement)
    }
}