package com.zmci.safetymonitoringapp.settings

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult
import com.amplifyframework.auth.options.AuthSignOutOptions
import com.amplifyframework.core.Amplify
import com.zmci.safetymonitoringapp.LoginActivity
import com.zmci.safetymonitoringapp.MainActivity
import com.zmci.safetymonitoringapp.R
import com.zmci.safetymonitoringapp.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

    private var _binding : FragmentSettingsBinding? = null
    private val binding by lazy { _binding!! }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val userEmail = view.findViewById<TextView>(R.id.userEmail)
        val userVerify = view.findViewById<TextView>(R.id.userVerify)
        val buttonTutorial = view.findViewById<Button>(R.id.buttonTutorial)
        val buttonLogout = view.findViewById<Button>(R.id.buttonLogout)
        val buttonPrivacy = view.findViewById<Button>(R.id.buttonPrivacy)

        buttonPrivacy.setOnClickListener {
            view.findNavController().navigate(R.id.action_navigation_settings_to_fragment_privacy)
        }

        buttonTutorial.setOnClickListener{
            val i = Intent(this.context,MainActivity::class.java)
            startActivity(i)
            activity?.finish()
        }

        buttonLogout.setOnClickListener {
            AlertDialog.Builder(this.requireContext())
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setPositiveButton("Yes", DialogInterface.OnClickListener{ dialog, which ->
                    try {
                    val options = AuthSignOutOptions.builder()
                        .globalSignOut(true)
                        .build()
                    Amplify.Auth.signOut(options) { signOutResult ->
                        when(signOutResult) {
                            is AWSCognitoAuthSignOutResult.CompleteSignOut -> {
                                val i = Intent(this.context, LoginActivity::class.java)
                                startActivity(i)
                                activity?.finish()
                            }
                            is AWSCognitoAuthSignOutResult.PartialSignOut -> {
                                // handle partial sign out
                            }
                            is AWSCognitoAuthSignOutResult.FailedSignOut -> {
                                // handle failed sign out
                            }
                        }
                    }} catch (e : Exception){
                        e.printStackTrace()
                    }
                })
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->  })
                .setIcon(R.drawable.ic_logout)
                .show()
        }

        Amplify.Auth.fetchUserAttributes(
            {user ->
                val currentEmailVerified = user[1].value.toString()
                val currentUserEmail = user[2].value.toString()
                Log.i("EmailVerified", currentEmailVerified)
                Log.i("UserEmail", currentUserEmail)
                userEmail.text = currentUserEmail
                if (currentEmailVerified == "true"){
                    userVerify.text = "Verified"
                } else {
                    userVerify.setTextColor(Color.RED)
                    userVerify.text = "Not verified"
                }

            },{
                Log.i("FetchUserAttribute", "Error fetch user attribute")
            }
        )

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}