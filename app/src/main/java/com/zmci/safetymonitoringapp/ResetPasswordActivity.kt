package com.zmci.safetymonitoringapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
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
            buttonReset.showLoading()
            if (etUsername.text.toString().isEmpty()){
                buttonReset.hideLoading()
                Snackbar.make(
                    binding.root,
                    "Fill out empty field",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                Amplify.Auth.resetPassword(etUsername.text.toString(),
                    { Log.i("AuthQuickstart", "Password reset OK: $it")
                        buttonReset.hideLoading()
                        Snackbar.make(
                            binding.root,
                            "Password reset success, check your email",
                            Snackbar.LENGTH_LONG
                        ).show()
                        val i = Intent(this, ConfirmResetPasswordActivity::class.java)
                        i.putExtra("username", etUsername.text.toString())
                        startActivity(i)
                        finish()
                    },
                    { Log.e("AuthQuickstart", "Password reset failed", it)
                        buttonReset.hideLoading()
                        Snackbar.make(
                            binding.root,
                            "Password reset failed",
                            Snackbar.LENGTH_LONG
                        ).show()
                    }
                )
            }
        }


    }
}