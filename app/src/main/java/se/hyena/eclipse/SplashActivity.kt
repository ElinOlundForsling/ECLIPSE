package se.hyena.eclipse

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val signedOutIntent = Intent(this, SignInActivity::class.java)
        val signedInIntent = Intent(this, MainActivity::class.java)

        if (FirebaseAuth.getInstance().currentUser == null)
            startActivity(signedOutIntent)
        else
            startActivity(signedInIntent)
        finish()
    }
}
