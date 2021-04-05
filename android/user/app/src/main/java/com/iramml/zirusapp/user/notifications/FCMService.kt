package com.iramml.zirusapp.user.notifications

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.iramml.zirusapp.user.model.AuthFirebaseModel

class FCMService: FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FIREBASE_NOTIFICATION", "From: ${remoteMessage.from}")

        if (remoteMessage.data.isNotEmpty()) {
            Log.d("FIREBASE_NOTIFICATION", "Message data payload: ${remoteMessage.data}")
        }

        remoteMessage.notification?.let {
            Log.d("FIREBASE_NOTIFICATION", "Message Notification Body: ${it.body}")
        }

    }
    override fun onNewToken(token: String) {
        val authFirebaseModel: AuthFirebaseModel = AuthFirebaseModel()
        authFirebaseModel.saveFirebaseToken(token)
    }
}