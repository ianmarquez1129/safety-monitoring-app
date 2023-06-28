package com.zmci.safetymonitoringapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify

class SignupActivity : AppCompatActivity() {

    private lateinit var buttonSignup : Button
    private lateinit var registerUsername : EditText
    private lateinit var registerEmail : EditText
    private lateinit var registerPassword : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        buttonSignup = findViewById(R.id.buttonSignup)
        registerUsername = findViewById(R.id.registerUsername)
        registerEmail = findViewById(R.id.registerEmail)
        registerPassword = findViewById(R.id.registerPassword)

        buttonSignup.setOnClickListener{
            val options = AuthSignUpOptions.builder()
                .userAttribute(AuthUserAttributeKey.email(), registerEmail.text.toString())
                .build()
            Amplify.Auth.signUp(registerUsername.text.toString(), registerPassword.text.toString(), options,
                { Log.i("AuthQuickStart", "Sign up succeeded: $it")
                    val i = Intent(this,ConfirmActivity::class.java)
                    i.putExtra("username", registerUsername.text.toString())
                    startActivity(i)
                },
                { Log.e ("AuthQuickStart", "Sign up failed", it)
                    val i = Intent(this,LoginActivity::class.java)
                    startActivity(i)
                    finish()
                }
            )
        }
    }
}