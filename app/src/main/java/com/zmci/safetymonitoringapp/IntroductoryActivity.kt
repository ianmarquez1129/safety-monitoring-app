package com.zmci.safetymonitoringapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
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
//            setupAuthButton(UserData)
            UserData.isSignedIn.observe(this, Observer<Boolean> { isSignedUp ->
                // update UI
                Log.i("TAG", "isSignedIn changed : $isSignedUp")

                if (isSignedUp) {
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
    private fun setupAuthButton(userData: UserData) {

        // register a click listener

        if (userData.isSignedIn.value!!) {
            val intent = Intent(this,DashboardActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


}