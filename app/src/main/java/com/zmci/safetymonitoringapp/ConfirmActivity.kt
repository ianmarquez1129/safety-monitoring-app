package com.zmci.safetymonitoringapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.EditText
import android.widget.FrameLayout
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
            if (editText.text.toString().isEmpty()){
                button.hideLoading()
                val snackBarView = Snackbar.make(binding.root, R.string.fill_out_empty_fields , Snackbar.LENGTH_LONG)
                val view = snackBarView.view
                val params = view.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.TOP
                view.layoutParams = params
                snackBarView.setBackgroundTint(Color.RED)
                snackBarView.show()
            } else {
                button.showLoading()
                Amplify.Auth.confirmSignUp(
                    username, editText.text.toString(),
                    { result ->
                        if (result.isSignUpComplete) {
                            Log.i("AuthQuickstart", "Confirm signUp succeeded")
                            button.hideLoading()
                            val snackBarView = Snackbar.make(binding.root, R.string.account_was_successfully_verified , Snackbar.LENGTH_LONG)
                            val view = snackBarView.view
                            val params = view.layoutParams as FrameLayout.LayoutParams
                            params.gravity = Gravity.TOP
                            view.layoutParams = params
                            snackBarView.setBackgroundTint(Color.GREEN)
                            snackBarView.show()
                            val i = Intent(this, LoginActivity::class.java)
                            startActivity(i)
                            finish()
                        } else {
                            Log.i("AuthQuickstart", "Confirm sign up not complete")
                            button.hideLoading()
                            val snackBarView = Snackbar.make(binding.root, R.string.invalid_code, Snackbar.LENGTH_LONG)
                            val view = snackBarView.view
                            val params = view.layoutParams as FrameLayout.LayoutParams
                            params.gravity = Gravity.TOP
                            view.layoutParams = params
                            snackBarView.setBackgroundTint(Color.RED)
                            snackBarView.show()
                        }
                    },
                    {
                        Log.e("AuthQuickstart", "Failed to confirm sign up", it)
                        button.hideLoading()
                        val snackBarView = Snackbar.make(binding.root, R.string.invalid_code , Snackbar.LENGTH_LONG)
                        val view = snackBarView.view
                        val params = view.layoutParams as FrameLayout.LayoutParams
                        params.gravity = Gravity.TOP
                        view.layoutParams = params
                        snackBarView.setBackgroundTint(Color.RED)
                        snackBarView.show()
                    }
                )
            }

        }
    }
}