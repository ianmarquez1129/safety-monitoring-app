package com.zmci.safetymonitoringapp.home.camera

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.kusu.loadingbutton.LoadingButton
import com.zmci.safetymonitoringapp.R
import com.zmci.safetymonitoringapp.databinding.FragmentWifiBinding
import com.zmci.safetymonitoringapp.home.camera.model.Post
import com.zmci.safetymonitoringapp.home.camera.repository.Repository

class WifiFragment : Fragment() {

    private var _binding : FragmentWifiBinding? = null
    private val binding by lazy { _binding!! }

    private lateinit var viewModel : WifiViewModel

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
            if (networkSSID.text.toString().isEmpty())
            {
                buttonPost.hideLoading()
                Snackbar.make(binding.root, "Fill out empty fields", Snackbar.LENGTH_SHORT).show()
            } else {
                // Do POST request here
                try {
                    val repository = Repository()
                    val viewModelFactory = WifiViewModelFactory(repository)
                    viewModel =
                        ViewModelProvider(this, viewModelFactory)[WifiViewModel::class.java]
                    val wifiCredentials = Post(networkSSID.text.toString(),networkPassword.text.toString())
                    viewModel.pushPost(wifiCredentials)
                    viewModel.myResponse.observe(this.viewLifecycleOwner, Observer { response ->
                        if (response.isSuccessful) {
                            Log.d("Main", response.body().toString())
                            Log.d("Main", response.code().toString())
                            Log.d("Main", response.message())
                            buttonPost.hideLoading()
                        } else {
                            Toast.makeText(activity, response.code(), Toast.LENGTH_SHORT).show()
                            buttonPost.hideLoading()
                        }
                    })
                } catch (e:Exception){
                    e.printStackTrace()
                }
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