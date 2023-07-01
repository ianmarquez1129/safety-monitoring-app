package com.zmci.safetymonitoringapp.home.detection

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zmci.safetymonitoringapp.R
import com.zmci.safetymonitoringapp.database.DatabaseHelper
import com.zmci.safetymonitoringapp.databinding.FragmentCameraLogsBinding
import com.zmci.safetymonitoringapp.home.detection.adapter.DetectionAdapter
import com.zmci.safetymonitoringapp.home.detection.utils.CAMERA_NAME_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.DETECTION_CAMERA_NAME_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.DETECTION_IMAGE_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.DETECTION_TIMESTAMP_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.DETECTION_VIOLATORS_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.TOTAL_VIOLATIONS_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.TOTAL_VIOLATORS_KEY

class CameraLogsFragment : Fragment() {

    private var _binding: FragmentCameraLogsBinding? = null
    private val binding by lazy { _binding!! }

    private lateinit var detectionAdapter: DetectionAdapter

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
        _binding = FragmentCameraLogsBinding.inflate(inflater,container,false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cameraLogs = view.findViewById<TextView>(R.id.cameraLogs)
        val rvCameraDetection = view.findViewById<RecyclerView>(R.id.rvCameraDetection)

        // Get arguments passed by HomeFragment
        val cameraName = arguments?.getString(CAMERA_NAME_KEY).toString()

        // set camera name as title
        cameraLogs.text = "$cameraName Logs"

        //set List
        try {
            val detectionList = databaseHelper.getDetectionOfCamera(requireContext(),cameraName)
            // set adapter
            detectionList.sortByDescending { it.id }
            detectionAdapter = DetectionAdapter(requireContext(), detectionList)
            //set find Id
            val rvReports: RecyclerView = rvCameraDetection
            //set recycler view adapter
            rvReports.layoutManager = LinearLayoutManager(this.context)
            rvReports.adapter = detectionAdapter

            val adapter = detectionAdapter
            rvReports.adapter = adapter
            adapter.setOnItemClickListener(object : DetectionAdapter.onItemClickListener {
                override fun onItemClick(position: Int) {
                    val image             = detectionList[position].image
                    val cameraName2        = detectionList[position].cameraName
                    val timestamp         = detectionList[position].timestamp
                    val violators         = detectionList[position].violators
                    val total_violations  = detectionList[position].total_violations
                    val total_violators   = detectionList[position].total_violators

                    val detectionBundle = bundleOf(
                        DETECTION_IMAGE_KEY      to image,
                        DETECTION_CAMERA_NAME_KEY to cameraName2,
                        DETECTION_TIMESTAMP_KEY  to timestamp,
                        DETECTION_VIOLATORS_KEY  to violators,
                        TOTAL_VIOLATIONS_KEY to total_violations,
                        TOTAL_VIOLATORS_KEY to total_violators)
                    findNavController().navigate(
                        R.id.action_fragment_camera_logs_to_fragment_detection_report, detectionBundle)
                }
            })
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}