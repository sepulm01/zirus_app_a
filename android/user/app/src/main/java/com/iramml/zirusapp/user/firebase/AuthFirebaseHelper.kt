package com.iramml.zirusapp.user.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.iramml.zirusapp.user.common.Common
import com.iramml.zirusapp.user.model.firebase.NormalUser

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

    fun signIn(email: String, password: String, signInListener: AuthListener.SignInListener) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                getCurrentUser(signInListener)
            }.addOnFailureListener {
                signInListener.onFailureListener(it)
            }
    }

    fun signUp(userDriver: NormalUser, password: String, signUpListener: AuthListener.SignUpListener) {
        firebaseAuth.createUserWithEmailAndPassword(userDriver.email, password)
            .addOnSuccessListener {
                insertSignUpData(userDriver, signUpListener)
            }
            .addOnFailureListener {
                signUpListener.onCreateUserFailureListener(it)
            }
    }

    private fun insertSignUpData(userDriver: NormalUser, signUpListener: AuthListener.SignUpListener) {
        val userID = FirebaseAuth.getInstance().currentUser?.uid


        normalUserReference.child(userID!!).setValue(userDriver)
                .addOnSuccessListener {
                    signUpListener.onCompleteListener()
                }.addOnFailureListener {
                    signUpListener.onInsertUserDataFailureListener(it)
                }
    }

    private fun getCurrentUser(signInListener: AuthListener.SignInListener) {
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

    fun getCurrentUser(currentUserListener: AuthListener.CurrentUserListener) {
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



}

