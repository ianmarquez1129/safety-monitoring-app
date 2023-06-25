package com.zmci.safetymonitoringapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView

class IntroductoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_introductory)

        val logo = findViewById<ImageView>(R.id.logo)
        logo.animate().scaleX(1.2f).setDuration(500).startDelay = 200
        logo.animate().scaleY(1.2f).setDuration(500).startDelay = 200

        supportActionBar?.hide()

        Handler().postDelayed({
            val intent = Intent(this,LoginActivity::class.java)
            startActivity(intent)
            finish()
        },3000)
    }
}