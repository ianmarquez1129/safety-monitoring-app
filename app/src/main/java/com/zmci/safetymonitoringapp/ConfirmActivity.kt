package com.zmci.safetymonitoringapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.core.Amplify
import com.google.android.material.snackbar.Snackbar
import com.kusu.loadingbutton.LoadingButton
import com.zmci.safetymonitoringapp.databinding.ActivityConfirmBinding

class ConfirmActivity : AppCompatActivity() {

    private lateinit var binding : ActivityConfirmBinding

    private lateinit var button: LoadingButton
    private lateinit var editText: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConfirmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra("username").toString()
        editText = findViewById(R.id.verificationCode)
        button = findViewById(R.id.buttonVerify)

        button.setOnClickListener {
            button.showLoading()
            if (editText.text.toString().isEmpty()){
                button.hideLoading()
                Snackbar.make(
                    binding.root,
                    "Fill out empty fields",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                Amplify.Auth.confirmSignUp(
                    username, editText.text.toString(),
                    { result ->
                        if (result.isSignUpComplete) {
                            Log.i("AuthQuickstart", "Confirm signUp succeeded")
                            button.hideLoading()
                            Snackbar.make(
                                binding.root,
                                "Account was successfully verified",
                                Snackbar.LENGTH_LONG
                            ).show()
                            val i = Intent(this, LoginActivity::class.java)
                            startActivity(i)
                            finish()
                        } else {
                            Log.i("AuthQuickstart", "Confirm sign up not complete")
                            button.hideLoading()
                            Snackbar.make(
                                binding.root,
                                "Invalid code",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    },
                    {
                        Log.e("AuthQuickstart", "Failed to confirm sign up", it)
                        button.hideLoading()
                        Snackbar.make(
                            binding.root,
                            "Invalid code",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                )
            }

        }
    }
}