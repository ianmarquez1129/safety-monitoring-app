package com.zmci.safetymonitoringapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
    }
}