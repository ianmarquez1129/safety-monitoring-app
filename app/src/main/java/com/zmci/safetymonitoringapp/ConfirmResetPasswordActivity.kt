package com.zmci.safetymonitoringapp

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.EditText
import android.widget.FrameLayout
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
            if (confirmationCode.text.toString().isEmpty() || newPassword.text.toString().isEmpty()){
                buttonSubmit.hideLoading()
                val snackBarView = Snackbar.make(binding.root, R.string.fill_out_empty_fields , Snackbar.LENGTH_LONG)
                val view = snackBarView.view
                val params = view.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.TOP
                view.layoutParams = params
                snackBarView.setBackgroundTint(Color.RED)
                snackBarView.show()
            } else {
                if (isValidPassword(newPassword.text.toString())){
                    buttonSubmit.showLoading()
                    Amplify.Auth.confirmResetPassword(username, newPassword.text.toString(), confirmationCode.text.toString(),
                        { Log.i("AuthQuickstart", "New password confirmed")
                            buttonSubmit.hideLoading()
                            val snackBarView = Snackbar.make(binding.root, R.string.account_reset_successful , Snackbar.LENGTH_LONG)
                            val view = snackBarView.view
                            val params = view.layoutParams as FrameLayout.LayoutParams
                            params.gravity = Gravity.TOP
                            view.layoutParams = params
                            snackBarView.setBackgroundTint(Color.GREEN)
                            snackBarView.show()
                            val i = Intent(this, LoginActivity::class.java)
                            startActivity(i)
                            finish()
                        },
                        { Log.e("AuthQuickstart", "Failed to confirm password reset", it)
                            buttonSubmit.hideLoading()
                            val snackBarView = Snackbar.make(binding.root, R.string.failed_to_confirm_password_reset , Snackbar.LENGTH_LONG)
                            val view = snackBarView.view
                            val params = view.layoutParams as FrameLayout.LayoutParams
                            params.gravity = Gravity.TOP
                            view.layoutParams = params
                            snackBarView.setBackgroundTint(Color.RED)
                            snackBarView.show()
                        }
                    )
                } else {
                    buttonSubmit.hideLoading()
                    val snackBarView = Snackbar.make(binding.root, R.string.please_provide_a_strong_password , Snackbar.LENGTH_LONG)
                    val view = snackBarView.view
                    val params = view.layoutParams as FrameLayout.LayoutParams
                    params.gravity = Gravity.TOP
                    view.layoutParams = params
                    snackBarView.setBackgroundTint(Color.BLUE)
                    snackBarView.show()
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