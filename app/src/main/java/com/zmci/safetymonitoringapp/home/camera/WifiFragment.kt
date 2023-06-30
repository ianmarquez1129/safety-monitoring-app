package com.zmci.safetymonitoringapp.home.camera

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.kusu.loadingbutton.LoadingButton
import com.zmci.safetymonitoringapp.R
import com.zmci.safetymonitoringapp.databinding.FragmentWifiBinding

class WifiFragment : Fragment() {

    private var _binding : FragmentWifiBinding? = null
    private val binding by lazy { _binding!! }

    private val viewModel : WifiViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWifiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val networkSSID = view.findViewById<EditText>(R.id.networkSSID)
        val networkPassword = view.findViewById<EditText>(R.id.networkPassword)
        val buttonPost = view.findViewById<LoadingButton>(R.id.buttonPost)

        //initialize load of web

        buttonPost.setOnClickListener {
            buttonPost.showLoading()
            if (networkSSID.text.toString().isEmpty() || networkPassword.text.toString().isEmpty())
            {
                buttonPost.hideLoading()
                Snackbar.make(binding.root, "Fill out empty fields", Snackbar.LENGTH_SHORT).show()
            } else {
                // Do POST request here
                // if success go to add camera credentials
                // else snackbar failed
                statusLED()

            }
        }

    }

    private fun statusLED() {
        val statusDialog = AlertDialog.Builder(this.requireContext())

        statusDialog.setTitle("LED Status")
        statusDialog.setIcon(R.drawable.ic_wifi)
        statusDialog.setMessage("Please wait while the device is connecting to the internet.\nWhat is the status of LED?")
        statusDialog.setPositiveButton("Green", DialogInterface.OnClickListener { dialog, _ ->
            //set task green
            view?.findNavController()?.navigate(R.id.action_fragment_wifi_to_fragment_camera_credentials)
            dialog.dismiss()
        })
        statusDialog.setNegativeButton("Red") { dialog, _ ->
            // set task red
            view?.findNavController()?.navigate(R.id.action_fragment_wifi_to_fragment_connect_camera)
            dialog.dismiss()
        }
        statusDialog.create()
        statusDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}