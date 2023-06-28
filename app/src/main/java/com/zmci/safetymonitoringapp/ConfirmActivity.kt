package com.zmci.safetymonitoringapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.amplifyframework.core.Amplify

class ConfirmActivity : AppCompatActivity() {

    private lateinit var button: Button
    private lateinit var editText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm)

        val username = intent.getStringExtra("username").toString()
        editText = findViewById(R.id.verificationCode)
        button = findViewById(R.id.buttonVerify)

        button.setOnClickListener {
            Amplify.Auth.confirmSignUp(
                username, editText.text.toString(),
                { result ->
                    if (result.isSignUpComplete) {
                        Log.i("AuthQuickstart", "Confirm signUp succeeded")
                        val i = Intent(this,LoginActivity::class.java)
                        startActivity(i)
                        finish()
                    } else {
                        Log.i("AuthQuickstart","Confirm sign up not complete")
                    }
                },
                { Log.e("AuthQuickstart", "Failed to confirm sign up", it) }
            )

        }
    }
}