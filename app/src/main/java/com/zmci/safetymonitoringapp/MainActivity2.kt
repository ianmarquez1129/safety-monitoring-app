package com.zmci.safetymonitoringapp

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Button

class MainActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)

        val btnTutorial = findViewById<Button>(R.id.btnTutorial)
        btnTutorial.setOnClickListener{
            val i = Intent(this,MainActivity::class.java)
            startActivity(i)
            finish()
        }

        //Vibration effect
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) { // Vibrator availability checking
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE)) // New vibrate method for API Level 26 or higher
                Log.d("tag","This device vibrates... from if")
            } else {
                vibrator.vibrate(500) // Vibrate method for below API Level 26
                Log.d("tag","This device vibrates... from else")
            }
        }
        //End of vibration effect
    }
}