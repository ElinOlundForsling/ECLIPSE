package se.hyena.eclipse.service

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import se.hyena.eclipse.util.FirestoreUtil
import java.lang.NullPointerException

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        if (FirebaseAuth.getInstance().currentUser != null)
            addTokenToFirestore(token)
    }
     companion object {
         fun addTokenToFirestore(newRegistrationToken: String?) {
             if (newRegistrationToken == null) throw NullPointerException("FCM token is null.")

             FirestoreUtil.getFCMRegistrationTokens { tokens ->
                 if (tokens.contains(newRegistrationToken))
                     return@getFCMRegistrationTokens

                 tokens.add(newRegistrationToken)
                 FirestoreUtil.setFCMRegistrationTokens(tokens)
             }
         }
     }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("FCM", "From: ${remoteMessage.from}")
        remoteMessage.data.isNotEmpty().let {
            Log.d("FCM", "Message data payload: " + remoteMessage.data)

        }
        remoteMessage.notification?.let {
            Log.d("FCM", "Message Notification Body: ${it.body}")
        }
        if (remoteMessage.notification != null) {
            Log.d("FCM", "FCM message received!")
        }
    }
}