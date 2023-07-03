package com.zmci.safetymonitoringapp.home.camera

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.kusu.loadingbutton.LoadingButton
import com.zmci.safetymonitoringapp.DashboardActivity
import com.zmci.safetymonitoringapp.R
import com.zmci.safetymonitoringapp.database.DatabaseHelper
import com.zmci.safetymonitoringapp.databinding.FragmentCameraCredentialsBinding
import com.zmci.safetymonitoringapp.home.detection.model.CameraData

class CameraCredentialsFragment : Fragment() {

    private var _binding : FragmentCameraCredentialsBinding? = null
    private val binding by lazy { _binding!! }

    companion object {
        lateinit var databaseHelper: DatabaseHelper
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelper = DatabaseHelper(this.requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                val snackBarView = Snackbar.make(binding.root, R.string.fill_out_empty_fields , Snackbar.LENGTH_LONG)
                val view1 = snackBarView.view
                val params = view1.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.TOP
                view1.layoutParams = params
                snackBarView.setBackgroundTint(Color.RED)
                snackBarView.show()
            } else {
                // set mqtt and add credentials to database
                val camera = CameraData()
                camera.cameraName = deviceName.text.toString()
                camera.MQTT_TOPIC = deviceUniqueID.text.toString()
                camera.MQTT_CLIENT_ID = java.util.UUID.randomUUID().toString()
                databaseHelper.addCamera(this.requireContext(), camera)

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