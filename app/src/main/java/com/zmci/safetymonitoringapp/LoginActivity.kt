package com.zmci.safetymonitoringapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import com.amplifyframework.core.Amplify

class LoginActivity : AppCompatActivity() {

    private lateinit var buttonLogin : Button
    private lateinit var username : EditText
    private lateinit var password : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val textViewSignup = findViewById<TextView>(R.id.textViewSignup)
        textViewSignup.setOnClickListener{
            val i = Intent(this,SignupActivity::class.java)
            startActivity(i)
        }

        buttonLogin = findViewById(R.id.buttonLogin)
        username = findViewById(R.id.editTextUsername)
        password = findViewById(R.id.editTextPassword)
        buttonLogin.setOnClickListener{
            Amplify.Auth.signIn(username.text.toString(), password.text.toString(),
                { result ->
                    if (result.isSignedIn) {
                        Log.i("AuthQuickstart", "Sign in succeeded")
                        val i = Intent(this, MainActivity::class.java)
                        i.putExtra("username", username.text.toString())
                        startActivity(i)
                        finish()
                    } else {
                        Log.i("AuthQuickstart", "Sign in not complete")
                    }
                },
                { Log.e("AuthQuickstart", "Failed to sign in", it) }
            )
//            setupAuthButton(UserData)
        }

//        UserData.isSignedIn.observe(this, Observer<Boolean> { isSignedUp ->
//            // update UI
//            Log.i("TAG", "isSignedIn changed : $isSignedUp")
//
//            if (isSignedUp) {
//                val i = Intent(this,MainActivity::class.java)
//                startActivity(i)
//                Log.i("AuthQuickstart", "Sign in succeeded")
//            } else {
//                Toast.makeText(this, "Incorrect username or password", Toast.LENGTH_SHORT).show()
//            }
//        })

    }
//    private fun setupAuthButton(userData: UserData) {
//
//        // register a click listener
//
//        if (userData.isSignedIn.value!!) {
//            Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show()
//        } else {
//            Toast.makeText(this, "Incorrect username or password", Toast.LENGTH_SHORT).show()
//        }
//    }
}