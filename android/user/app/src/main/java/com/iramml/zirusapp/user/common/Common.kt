package com.iramml.zirusapp.user.common

import com.iramml.zirusapp.user.model.firebase.NormalUser

class Common {
    companion object {
        const val NormalUserInfoTable = "normal_user_information"
        const val RequirementUserTable = "requirement_user"
        const val AllRequirementsTable = "all_requirements"
        var currentUser: NormalUser? = null
    }
}