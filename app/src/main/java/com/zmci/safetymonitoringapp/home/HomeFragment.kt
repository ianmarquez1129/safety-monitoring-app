package com.zmci.safetymonitoringapp.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult
import com.amplifyframework.auth.options.AuthSignOutOptions
import com.amplifyframework.core.Amplify
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zmci.safetymonitoringapp.LoginActivity
import com.zmci.safetymonitoringapp.R
import com.zmci.safetymonitoringapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding by lazy { _binding!! }

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnSignOut = view.findViewById<FloatingActionButton>(R.id.btnSignOut)
        btnSignOut.setOnClickListener{
            val options = AuthSignOutOptions.builder()
                .globalSignOut(true)
                .build()

            Amplify.Auth.signOut(options) { signOutResult ->
                when(signOutResult) {
                    is AWSCognitoAuthSignOutResult.CompleteSignOut -> {
                        val i = Intent(this.context,LoginActivity::class.java)
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
            }
        }

        val addingBtn = view.findViewById<FloatingActionButton>(R.id.addingBtn)
        addingBtn.setOnClickListener{
            view.findNavController().navigate(R.id.action_navigation_home_to_fragment_add_camera)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}