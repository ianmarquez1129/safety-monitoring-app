package com.zmci.safetymonitoringapp.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zmci.safetymonitoringapp.R
import com.zmci.safetymonitoringapp.database.DatabaseHelper
import com.zmci.safetymonitoringapp.databinding.FragmentHomeBinding
import com.zmci.safetymonitoringapp.home.detection.adapter.CameraAdapter
import com.zmci.safetymonitoringapp.home.detection.utils.CAMERA_NAME_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.MQTT_CLIENT_ID_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.MQTT_TOPIC_KEY

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding by lazy { _binding!! }
    companion object {
        lateinit var databaseHelper: DatabaseHelper
    }
    private lateinit var cameraAdapter: CameraAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelper = DatabaseHelper(this.requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val addingBtn = view.findViewById<FloatingActionButton>(R.id.addingBtn)
        addingBtn.setOnClickListener{
            checkAddDevice()
        }

        try {
            //RecyclerView
            //set List
            val cameraList = databaseHelper.getAllCamera(requireContext())
            cameraAdapter = CameraAdapter(requireContext(), cameraList)
            //set find Id
            val recv: RecyclerView = view.findViewById(R.id.mRecycler)
            //set recycler view adapter
            recv.layoutManager = LinearLayoutManager(this.context)
            recv.adapter = cameraAdapter

            val adapter = cameraAdapter
            recv.adapter = adapter
            adapter.setOnItemClickListener(object : CameraAdapter.onItemClickListener {
                override fun onItemClick(position: Int) {
                    val cameraName = cameraList[position].cameraName
                    val topic = cameraList[position].MQTT_TOPIC
                    val clientID = cameraList[position].MQTT_CLIENT_ID

                    val mqttCredentialsBundle = bundleOf(
                        CAMERA_NAME_KEY to cameraName,
                        MQTT_TOPIC_KEY to topic,
                        MQTT_CLIENT_ID_KEY to clientID
                    )
                    findNavController().navigate(
                        R.id.action_navigation_home_to_fragment_client, mqttCredentialsBundle
                    )
                }
            })
        } catch (e : Exception) {
            e.printStackTrace()
        }

    }

    private fun checkAddDevice() {
        val deviceDialog = AlertDialog.Builder(this.requireContext())
        deviceDialog.setTitle("Add a device")
        deviceDialog.setItems(R.array.add_device) { dialog, which ->
            when (which) {
                0 -> { findNavController().navigate(R.id.action_navigation_home_to_fragment_add_camera) }
                1 -> { findNavController().navigate(R.id.action_navigation_home_to_fragment_camera_credentials) }
            }
        }
        deviceDialog.create()
        deviceDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}