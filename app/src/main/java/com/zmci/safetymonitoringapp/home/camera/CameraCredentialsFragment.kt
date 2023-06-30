package com.zmci.safetymonitoringapp.home.camera

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.kusu.loadingbutton.LoadingButton
import com.zmci.safetymonitoringapp.DashboardActivity
import com.zmci.safetymonitoringapp.R
import com.zmci.safetymonitoringapp.databinding.FragmentCameraCredentialsBinding

class CameraCredentialsFragment : Fragment() {

    private var _binding : FragmentCameraCredentialsBinding? = null
    private val binding by lazy { _binding!! }

    private val viewModel : CameraCredentialsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCameraCredentialsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val deviceName = view.findViewById<EditText>(R.id.deviceName)
        val deviceUniqueID = view.findViewById<EditText>(R.id.deviceUniqueID)
        val buttonCreate = view.findViewById<LoadingButton>(R.id.buttonCreate)

        buttonCreate.setOnClickListener {
            buttonCreate.showLoading()
            if (deviceName.text.toString().isEmpty() || deviceUniqueID.text.toString().isEmpty()) {
                buttonCreate.hideLoading()
                Snackbar.make(binding.root, "Fill out empty fields", Snackbar.LENGTH_SHORT).show()
            } else {
                // set mqtt
                confirmDeviceCreated()
            }
        }


    }

    private fun confirmDeviceCreated() {
        val deviceDialog = AlertDialog.Builder(this.requireContext())

        deviceDialog.setTitle("Device Added Successfully")
        deviceDialog.setIcon(R.drawable.ic_check)
        deviceDialog.setPositiveButton("PROCEED", DialogInterface.OnClickListener { dialog, _ ->
            val i = Intent(this.context, DashboardActivity::class.java)
            startActivity(i)
            activity?.finish()
            dialog.dismiss()
        })
        deviceDialog.create()
        deviceDialog.show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}