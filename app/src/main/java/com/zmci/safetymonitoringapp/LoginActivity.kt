package com.zmci.safetymonitoringapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.core.Amplify
import com.google.android.material.snackbar.Snackbar
import com.kusu.loadingbutton.LoadingButton
import com.zmci.safetymonitoringapp.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding

    private lateinit var buttonLogin : LoadingButton
    private lateinit var username : EditText
    private lateinit var password : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val textViewSignup = findViewById<TextView>(R.id.textViewSignup)
        textViewSignup.setOnClickListener{
            val i = Intent(this,SignupActivity::class.java)
            startActivity(i)
        }

        buttonLogin = findViewById(R.id.buttonLogin)
        username = findViewById(R.id.editTextUsername)
        password = findViewById(R.id.editTextPassword)
        buttonLogin.setOnClickListener{
            buttonLogin.showLoading()
            if (username.text.toString().isEmpty() || password.text.toString().isEmpty()){
                buttonLogin.hideLoading()
                Snackbar.make(
                    binding.root,
                    "Fill out empty fields",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                Amplify.Auth.signIn(username.text.toString(), password.text.toString(),
                    { result ->
                        if (result.isSignedIn) {
                            Log.i("AuthQuickstart", "Sign in succeeded")
                            buttonLogin.hideLoading()
                            Snackbar.make(binding.root, "Login success", Snackbar.LENGTH_SHORT)
                                .show()
                            val i = Intent(this, MainActivity::class.java)
                            i.putExtra("username", username.text.toString())
                            startActivity(i)
                            finish()
                        } else {
                            Log.i("AuthQuickstart", "Sign in not complete")
                            buttonLogin.hideLoading()
                            Snackbar.make(binding.root, "Login not complete", Snackbar.LENGTH_SHORT)
                                .show()
                        }
                    },
                    {
                        Log.e("AuthQuickstart", "Failed to sign in", it)
                        buttonLogin.hideLoading()
                        Snackbar.make(binding.root, "Username or Password is Incorrect", Snackbar.LENGTH_SHORT).show()
                    }
                )
            }
        }
    }
}