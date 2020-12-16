package com.iramml.zirusapp.user.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError
import com.iramml.zirusapp.user.R
import com.iramml.zirusapp.user.helper.AuthFirebaseHelper
import com.iramml.zirusapp.user.model.NormalUser

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler(Looper.getMainLooper()).postDelayed({
            val firebaseAuth = FirebaseAuth.getInstance()

            if (firebaseAuth.uid != null && firebaseAuth.uid != "") {
                AuthFirebaseHelper().getCurrentUser(object: AuthFirebaseHelper.CurrentUserListener {
                    override fun onCompleteListener(user: NormalUser) {
                        startActivity(Intent(this@SplashScreenActivity, HomeActivity::class.java))
                        finish()
                    }

                    override fun onCancelledGetCurrentUserListener(exception: DatabaseError) {

                    }

                })

            } else {
                startActivity(Intent(this@SplashScreenActivity, SignInActivity::class.java))
                finish()
            }


        }, 800)
    }
}