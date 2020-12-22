package com.iramml.zirusapp.user.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.iramml.zirusapp.user.common.Common
import com.iramml.zirusapp.user.model.NormalUser
import java.lang.Exception

class AuthFirebaseHelper {
    private val firebaseAuth: FirebaseAuth
    private val normalUserReference: DatabaseReference

    companion object {
        fun logout() {
            val firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.signOut()
        }
    }

    init {
        val firebaseDatabase = FirebaseDatabase.getInstance()
        firebaseAuth = FirebaseAuth.getInstance()

        normalUserReference = firebaseDatabase.getReference(Common.NormalUserInfoTable)
    }

    fun signIn(email: String, password: String, signInListener: SignInListener) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                getCurrentUser(signInListener)
            }.addOnFailureListener {
                signInListener.onFailureListener(it)
            }
    }

    fun signUp(userDriver: NormalUser, password: String, signUpListener: SignUpListener) {
        firebaseAuth.createUserWithEmailAndPassword(userDriver.email, password)
            .addOnSuccessListener {
                insertSignUpData(userDriver, signUpListener)
            }
            .addOnFailureListener {
                signUpListener.onCreateUserFailureListener(it)
            }
    }

    private fun insertSignUpData(userDriver: NormalUser, signUpListener: SignUpListener) {
        val userID = FirebaseAuth.getInstance().currentUser?.uid


        normalUserReference.child(userID!!).setValue(userDriver)
                .addOnSuccessListener {
                    signUpListener.onCompleteListener()
                }.addOnFailureListener {
                    signUpListener.onInsertUserDataFailureListener(it)
                }
    }

    private fun getCurrentUser(signInListener: SignInListener) {
        val userID = FirebaseAuth.getInstance().currentUser?.uid

        normalUserReference.child(userID!!).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val normalUser = snapshot.getValue(NormalUser::class.java)
                signInListener.onCompleteListener(normalUser!!)
            }

            override fun onCancelled(error: DatabaseError) {
                signInListener.onCancelledGetCurrentUserListener(error)
            }

        })
    }

    fun getCurrentUser(currentUserListener: CurrentUserListener) {
        val userID = FirebaseAuth.getInstance().currentUser?.uid

        normalUserReference.child(userID!!).addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val normalUser = snapshot.getValue(NormalUser::class.java)
                currentUserListener.onCompleteListener(normalUser!!)
            }

            override fun onCancelled(error: DatabaseError) {
                currentUserListener.onCancelledGetCurrentUserListener(error)
            }

        })
    }

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

