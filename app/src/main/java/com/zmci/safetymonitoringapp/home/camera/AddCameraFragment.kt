package com.zmci.safetymonitoringapp.home.camera

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.kusu.loadingbutton.LoadingButton
import com.zmci.safetymonitoringapp.R
import com.zmci.safetymonitoringapp.databinding.FragmentAddCameraBinding

class AddCameraFragment : Fragment() {

    private var _binding : FragmentAddCameraBinding? = null
    private val binding by lazy { _binding!! }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddCameraBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val buttonNext = view.findViewById<LoadingButton>(R.id.buttonNext)
        buttonNext.setOnClickListener {
            view.findNavController().navigate(R.id.action_fragment_add_camera_to_fragment_connect_camera)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}