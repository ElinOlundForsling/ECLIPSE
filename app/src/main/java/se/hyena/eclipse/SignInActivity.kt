package se.hyena.eclipse

import android.app.Activity
import android.content.Intent
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_sign_in.*
import se.hyena.eclipse.service.MyFirebaseMessagingService
import se.hyena.eclipse.util.FirestoreUtil

class SignInActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 1

    private val signInProviders =
        listOf(
            AuthUI.IdpConfig.EmailBuilder()
                .setAllowNewAccounts(true)
                .setRequireName(true)
                .build())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        account_sign_in.setOnClickListener {
            val intent = AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(signInProviders)
                .build()
            startActivityForResult(intent, RC_SIGN_IN)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                //TODO: Progress bar
                //TODO: Initialize current user in Firestore
                FirestoreUtil.initCurrentUserIfFirstTime {
                    val helloIntent = Intent(this, MainActivity::class.java)
                        .apply {
                            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        }
                    startActivity(helloIntent)
                    val registrationToken = FirebaseInstanceId.getInstance().instanceId
                        .addOnCompleteListener { task ->
                            if (!task.isSuccessful) {
                                Log.w("FCM", "getInstanceId failed", task.exception)
                                return@addOnCompleteListener
                            }
                            val token = task.result?.token
                            MyFirebaseMessagingService.addTokenToFirestore(token)
                        }
                }

            }
            else if (resultCode == Activity.RESULT_CANCELED) {
                if (response == null) return

                when (response.error?.errorCode) {
                    ErrorCodes.NO_NETWORK ->
                        Snackbar.make(findViewById(R.id.content),"No network", Snackbar.LENGTH_SHORT)
                    ErrorCodes.UNKNOWN_ERROR ->
                        Snackbar.make(findViewById(R.id.content),"Unknown Error", Snackbar.LENGTH_SHORT)

                }
            }
        }
    }
}
