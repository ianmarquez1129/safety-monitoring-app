package com.zmci.safetymonitoringapp.home.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.kusu.loadingbutton.LoadingButton
import com.zmci.safetymonitoringapp.R
import com.zmci.safetymonitoringapp.databinding.FragmentConnectCameraBinding

class ConnectCameraFragment : Fragment() {

    private var _binding : FragmentConnectCameraBinding? = null
    private val binding by lazy { _binding!! }

    private val viewModel : ConnectCameraViewModel by viewModels()

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
            view.findNavController().navigate(R.id.action_fragment_connect_camera_to_fragment_wifi)
            buttonConnected.hideLoading()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}