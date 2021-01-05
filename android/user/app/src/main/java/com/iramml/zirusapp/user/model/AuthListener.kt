package com.iramml.zirusapp.user.model

import com.google.firebase.database.DatabaseError
import com.iramml.zirusapp.user.model.schema.firebase.NormalUser
import java.lang.Exception

interface AuthListener {
    interface SignUpListener {
        fun onCompleteListener()
        fun onCreateUserFailureListener(exception: Exception)
        fun onInsertUserDataFailureListener(exception: Exception)
    }

    interface SignInListener {
        fun onCompleteListener(user: NormalUser)
        fun onFailureListener(exception: Exception)
        fun onCancelledGetCurrentUserListener(exception: DatabaseError)
    }

    interface CurrentUserListener {
        fun onCompleteListener(user: NormalUser)
        fun onCancelledGetCurrentUserListener(exception: DatabaseError)
    }
}