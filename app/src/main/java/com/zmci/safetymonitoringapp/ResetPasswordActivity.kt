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
import com.zmci.safetymonitoringapp.databinding.ActivityResetPasswordBinding

class ResetPasswordActivity : AppCompatActivity() {

    private lateinit var binding : ActivityResetPasswordBinding

    private lateinit var buttonReset : LoadingButton
    private lateinit var etUsername : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        etUsername = findViewById(R.id.etUsername)
        buttonReset = findViewById(R.id.buttonReset)
        buttonReset.setOnClickListener {
            if (etUsername.text.toString().isEmpty()){
                buttonReset.hideLoading()
                val snackBarView = Snackbar.make(binding.root, R.string.fill_out_empty_fields , Snackbar.LENGTH_LONG)
                val view = snackBarView.view
                val params = view.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.TOP
                view.layoutParams = params
                snackBarView.setBackgroundTint(Color.RED)
                snackBarView.show()
            } else {
                buttonReset.showLoading()
                Amplify.Auth.resetPassword(etUsername.text.toString(),
                    { Log.i("AuthQuickstart", "Password reset OK: $it")
                        buttonReset.hideLoading()
                        val snackBarView = Snackbar.make(binding.root, R.string.password_reset_success_check_your_email , Snackbar.LENGTH_LONG)
                        val view = snackBarView.view
                        val params = view.layoutParams as FrameLayout.LayoutParams
                        params.gravity = Gravity.TOP
                        view.layoutParams = params
                        snackBarView.setBackgroundTint(Color.GREEN)
                        snackBarView.show()
                        val i = Intent(this, ConfirmResetPasswordActivity::class.java)
                        i.putExtra("username", etUsername.text.toString())
                        startActivity(i)
                        finish()
                    },
                    { Log.e("AuthQuickstart", "Password reset failed", it)
                        buttonReset.hideLoading()
                        val snackBarView = Snackbar.make(binding.root, R.string.password_reset_failed , Snackbar.LENGTH_LONG)
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