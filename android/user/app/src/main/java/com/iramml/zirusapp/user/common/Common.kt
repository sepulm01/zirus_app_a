package com.iramml.zirusapp.user.common

import com.iramml.zirusapp.user.model.NormalUser

class Common {
    companion object {
        const val NormalUserInfoTable = "normal_user_information"
        var currentUser: NormalUser? = null
    }
}