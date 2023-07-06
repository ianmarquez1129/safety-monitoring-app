package com.zmci.safetymonitoringapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer

class IntroductoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introductory)

        val logo = findViewById<ImageView>(R.id.logo)
        logo.animate().scaleX(1.2f).setDuration(500).startDelay = 200
        logo.animate().scaleY(1.2f).setDuration(500).startDelay = 200

        supportActionBar?.hide()

        Handler().postDelayed({
            UserData.isSignedIn.observe(this, Observer<Boolean> { isSignedUp ->
                // update UI
                Log.i("TAG", "isSignedIn changed : $isSignedUp")

                if (isSignedUp) {
                    Backend.fetchUserAttributes()
                    val i = Intent(this,DashboardActivity::class.java)
                    startActivity(i)
                    Log.i("AuthQuickstart", "Sign in succeeded")
                    finish()
                } else {
                    val i = Intent(this,LoginActivity::class.java)
                    startActivity(i)
                    finish()
                }
            })
        },3000)
    }

}