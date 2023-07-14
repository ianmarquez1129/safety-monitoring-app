package com.zmci.safetymonitoringapp.home.camera

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.core.Amplify
import com.google.android.material.snackbar.Snackbar
import com.kusu.loadingbutton.LoadingButton
import com.zmci.safetymonitoringapp.DashboardActivity
import com.zmci.safetymonitoringapp.R
import com.zmci.safetymonitoringapp.database.DatabaseHelper
import com.zmci.safetymonitoringapp.databinding.FragmentCameraCredentialsBinding
import com.zmci.safetymonitoringapp.home.detection.model.CameraData
import org.json.JSONArray

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
        val devicePassword = view.findViewById<EditText>(R.id.devicePassword)
        val buttonCreate = view.findViewById<LoadingButton>(R.id.buttonCreate)

        buttonCreate.setOnClickListener {
            buttonCreate.showLoading()
            if (deviceName.text.toString().isEmpty() || deviceUniqueID.text.toString().isEmpty() || devicePassword.text.toString().isEmpty()) {
                buttonCreate.hideLoading()
                val snackBarView = Snackbar.make(binding.root, R.string.fill_out_empty_fields , Snackbar.LENGTH_LONG)
                val view1 = snackBarView.view
                val params = view1.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.TOP
                view1.layoutParams = params
                snackBarView.setBackgroundTint(Color.RED)
                snackBarView.show()
            } else {
                try {

                    val options = RestOptions.builder()
                        .addPath("/getDevice")
                        .addBody(
                            ("{\"uuid\":\"${deviceUniqueID.text}\"," +
                                    "\"password\":\"${devicePassword.text}\"" +
                                    "}").encodeToByteArray()
                        )
                        .build()
                    Log.i("OPTIONS","{\"uuid\":\"${deviceUniqueID.text}\"," +
                            "\"password\":\"${devicePassword.text}\"" +
                            "}")

                    Amplify.API.post(options,
                        {
                            Log.i("MyAmplifyApp", "POST succeeded: ${it.data.asString()}")
                            val data = JSONArray(it.data.asString())
                            try {
                                for (i in 0 until data.length()) {
                                    val camera = CameraData()
                                    val item = data.getJSONObject(i)
                                    val status = item.getString("status")
                                    if (status == "success") {
                                        camera.cameraName = deviceName.text.toString()
                                        camera.MQTT_CLIENT_ID =
                                            java.util.UUID.randomUUID().toString()
                                        camera.MQTT_TOPIC = deviceUniqueID.text.toString()
                                        camera.MQTT_PUB_TOPIC = item.getString("pub_topic")
                                        camera.MQTT_SET_TOPIC = item.getString("set_topic")
                                        databaseHelper.addCamera(this.requireContext(), camera)

                                        val snackBarView = Snackbar.make(
                                            binding.root,
                                            "Device Added Successfully",
                                            Snackbar.LENGTH_LONG
                                        )
                                        val view1 = snackBarView.view
                                        val params = view1.layoutParams as FrameLayout.LayoutParams
                                        params.gravity = Gravity.TOP
                                        view1.layoutParams = params
                                        snackBarView.setBackgroundTint(Color.GREEN)
                                        snackBarView.show()
                                        buttonCreate.hideLoading()
                                        val i = Intent(this.context, DashboardActivity::class.java)
                                        startActivity(i)
                                        activity?.finish()
                                    } else {
                                        val snackBarView = Snackbar.make(binding.root, "Device credentials are invalid" , Snackbar.LENGTH_LONG)
                                        val view1 = snackBarView.view
                                        val params = view1.layoutParams as FrameLayout.LayoutParams
                                        params.gravity = Gravity.TOP
                                        view1.layoutParams = params
                                        snackBarView.setBackgroundTint(Color.RED)
                                        snackBarView.show()
                                    }
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                val snackBarView = Snackbar.make(binding.root, "Device credentials are invalid" , Snackbar.LENGTH_LONG)
                                val view1 = snackBarView.view
                                val params = view1.layoutParams as FrameLayout.LayoutParams
                                params.gravity = Gravity.TOP
                                view1.layoutParams = params
                                snackBarView.setBackgroundTint(Color.RED)
                                snackBarView.show()
                            }
                        },
                        { Log.e("MyAmplifyApp", "POST failed", it)
                            val snackBarView = Snackbar.make(binding.root, "Device credentials are invalid" , Snackbar.LENGTH_LONG)
                            val view1 = snackBarView.view
                            val params = view1.layoutParams as FrameLayout.LayoutParams
                            params.gravity = Gravity.TOP
                            view1.layoutParams = params
                            snackBarView.setBackgroundTint(Color.RED)
                            snackBarView.show()
                        }
                    )
                    buttonCreate.hideLoading()
                } catch (e : Exception){
                    e.printStackTrace()
                }

                // set mqtt and add credentials to database
//                val camera = CameraData()
//                camera.cameraName = deviceName.text.toString()
//                camera.MQTT_TOPIC = deviceUniqueID.text.toString()
//                camera.MQTT_CLIENT_ID = java.util.UUID.randomUUID().toString()
//                databaseHelper.addCamera(this.requireContext(), camera)
//
//                confirmDeviceCreated()
            }
        }


    }

//    private fun confirmDeviceCreated() {
//        val deviceDialog = AlertDialog.Builder(this.requireContext())
//
//        deviceDialog.setTitle("Device Added Successfully")
//        deviceDialog.setIcon(R.drawable.ic_check)
//        deviceDialog.setPositiveButton("PROCEED", DialogInterface.OnClickListener { dialog, _ ->
//            val i = Intent(this.context, DashboardActivity::class.java)
//            startActivity(i)
//            activity?.finish()
//            dialog.dismiss()
//        })
//        deviceDialog.create()
//        deviceDialog.show()
//    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}