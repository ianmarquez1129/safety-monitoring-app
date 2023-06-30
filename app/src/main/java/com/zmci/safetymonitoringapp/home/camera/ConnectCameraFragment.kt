package com.zmci.safetymonitoringapp.home.camera

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.kusu.loadingbutton.LoadingButton
import com.zmci.safetymonitoringapp.R
import com.zmci.safetymonitoringapp.databinding.FragmentConnectCameraBinding
import com.zmci.safetymonitoringapp.home.camera.repository.Repository

class ConnectCameraFragment : Fragment() {

    private var _binding : FragmentConnectCameraBinding? = null
    private val binding by lazy { _binding!! }

    private lateinit var viewModel : WifiViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentConnectCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val buttonConnected = view.findViewById<LoadingButton>(R.id.buttonConnected)
        buttonConnected.setOnClickListener {
            buttonConnected.showLoading()
            try {
                val repository = Repository()
                val viewModelFactory = ConnectCameraViewModelFactory(repository)
                viewModel =
                    ViewModelProvider(this, viewModelFactory)[WifiViewModel::class.java]
                viewModel.getPost()
                viewModel.myResponse.observe(this.viewLifecycleOwner, Observer { response ->
                    if (response.isSuccessful) {
                        Log.d("Main", response.body().toString())
                        Log.d("Main", response.code().toString())
                        Log.d("Main", response.message())
                        buttonConnected.hideLoading()
                        view.findNavController()
                            .navigate(R.id.action_fragment_connect_camera_to_fragment_wifi)
                    } else {
                        Toast.makeText(activity, response.code(), Toast.LENGTH_SHORT).show()
                        buttonConnected.hideLoading()
                    }
                })
            } catch (e:Exception){
                e.printStackTrace()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}