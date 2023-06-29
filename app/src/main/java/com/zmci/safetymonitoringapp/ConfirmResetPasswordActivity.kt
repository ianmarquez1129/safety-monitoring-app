package com.zmci.safetymonitoringapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import com.amplifyframework.core.Amplify
import com.google.android.material.snackbar.Snackbar
import com.kusu.loadingbutton.LoadingButton
import com.zmci.safetymonitoringapp.databinding.ActivityConfirmResetPasswordBinding
import java.util.regex.Pattern

class ConfirmResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding : ActivityConfirmResetPasswordBinding

    private lateinit var confirmationCode : EditText
    private lateinit var newPassword : EditText
    private lateinit var buttonSubmit : LoadingButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConfirmResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra("username").toString()

        confirmationCode = findViewById(R.id.confirmationCode)
        newPassword = findViewById(R.id.newPassword)
        buttonSubmit = findViewById(R.id.buttonSubmit)

        buttonSubmit.setOnClickListener {
            buttonSubmit.showLoading()
            if (confirmationCode.text.toString().isEmpty() || newPassword.text.toString().isEmpty()){
                buttonSubmit.hideLoading()
                Snackbar.make(
                    binding.root,
                    "Fill out empty fields",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                if (isValidPassword(newPassword.text.toString())){
                    Amplify.Auth.confirmResetPassword(username, newPassword.text.toString(), confirmationCode.text.toString(),
                        { Log.i("AuthQuickstart", "New password confirmed")
                            buttonSubmit.hideLoading()
                            Snackbar.make(
                                binding.root,
                                "Account reset successful",
                                Snackbar.LENGTH_LONG
                            ).show()
                            val i = Intent(this, LoginActivity::class.java)
                            startActivity(i)
                            finish()
                        },
                        { Log.e("AuthQuickstart", "Failed to confirm password reset", it)
                            buttonSubmit.hideLoading()
                            Snackbar.make(
                                binding.root,
                                "Failed to confirm password reset",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    )
                } else {
                    buttonSubmit.hideLoading()
                    Snackbar.make(
                        binding.root,
                        "Please provide a strong password",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }

            }
        }

    }
    private fun isValidPassword(password: String): Boolean {

        val passwordPattern = "^(?!\\s+)(?!.*\\s+\$)(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[\\^\$*.{}()?\"!@#%&/\\\\,><':;|_~`=+\\- ])[A-Za-z0-9^\$*.{}()?\"!@#%&/\\\\,><':;|_~`=+\\- ]{8,256}\$"

        val pattern = Pattern.compile(passwordPattern)
        val matcher = pattern.matcher(password)

        return matcher.matches()
    }
}