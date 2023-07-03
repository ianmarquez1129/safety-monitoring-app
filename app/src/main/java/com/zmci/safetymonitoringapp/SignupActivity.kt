package com.zmci.safetymonitoringapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.amplifyframework.auth.AuthUserAttributeKey
import com.amplifyframework.auth.options.AuthSignUpOptions
import com.amplifyframework.core.Amplify
import com.google.android.material.snackbar.Snackbar
import com.kusu.loadingbutton.LoadingButton
import com.zmci.safetymonitoringapp.databinding.ActivitySignupBinding
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySignupBinding

    private lateinit var buttonSignup : LoadingButton
    private lateinit var registerUsername : EditText
    private lateinit var registerEmail : EditText
    private lateinit var registerPassword : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        buttonSignup = findViewById(R.id.buttonSignup)
        registerUsername = findViewById(R.id.registerUsername)
        registerEmail = findViewById(R.id.registerEmail)
        registerPassword = findViewById(R.id.registerPassword)

        buttonSignup.setOnClickListener{
            buttonSignup.showLoading()
            if (registerUsername.text.toString().isEmpty() || registerEmail.text.toString().isEmpty() || registerPassword.text.toString().isEmpty()){
                buttonSignup.hideLoading()
                val snackBarView = Snackbar.make(binding.root, "Fill out empty fields" , Snackbar.LENGTH_LONG)
                val view = snackBarView.view
                val params = view.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.TOP
                view.layoutParams = params
                snackBarView.setBackgroundTint(Color.RED)
                snackBarView.show()
            } else {
                if (isValidPassword(registerPassword.text.toString())) {
                    val options = AuthSignUpOptions.builder()
                        .userAttribute(AuthUserAttributeKey.email(), registerEmail.text.toString())
                        .build()
                    Amplify.Auth.signUp(registerUsername.text.toString(),
                        registerPassword.text.toString(),
                        options,
                        {
                            Log.i("AuthQuickStart", "Sign up succeeded: $it")
                            buttonSignup.hideLoading()
                            val snackBarView = Snackbar.make(binding.root, R.string.verify_your_account , Snackbar.LENGTH_LONG)
                            val view = snackBarView.view
                            val params = view.layoutParams as FrameLayout.LayoutParams
                            params.gravity = Gravity.TOP
                            view.layoutParams = params
                            snackBarView.setBackgroundTint(Color.GREEN)
                            snackBarView.show()
                            val i = Intent(this, ConfirmActivity::class.java)
                            i.putExtra("username", registerUsername.text.toString())
                            startActivity(i)
                        },
                        {
                            Log.e("AuthQuickStart", "Sign up failed", it)
                            buttonSignup.hideLoading()
                            val snackBarView = Snackbar.make(binding.root, R.string.sign_up_failed , Snackbar.LENGTH_LONG)
                            val view = snackBarView.view
                            val params = view.layoutParams as FrameLayout.LayoutParams
                            params.gravity = Gravity.TOP
                            view.layoutParams = params
                            snackBarView.setBackgroundTint(Color.RED)
                            snackBarView.show()
                        }
                    )
                } else {
                    val snackBarView = Snackbar.make(binding.root, R.string.please_provide_a_strong_password , Snackbar.LENGTH_LONG)
                    val view = snackBarView.view
                    val params = view.layoutParams as FrameLayout.LayoutParams
                    params.gravity = Gravity.TOP
                    view.layoutParams = params
                    snackBarView.setBackgroundTint(Color.BLUE)
                    snackBarView.show()
                    buttonSignup.hideLoading()
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