package com.zmci.safetymonitoringapp.logs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.core.Amplify
import com.zmci.safetymonitoringapp.R
import com.zmci.safetymonitoringapp.UserData
import com.zmci.safetymonitoringapp.database.DatabaseHelper
import com.zmci.safetymonitoringapp.databinding.FragmentLogsBinding
import com.zmci.safetymonitoringapp.home.detection.adapter.DetectionAdapter
import com.zmci.safetymonitoringapp.home.detection.model.Detection
import com.zmci.safetymonitoringapp.home.detection.utils.DETECTION_CAMERA_NAME_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.DETECTION_IMAGE_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.DETECTION_TIMESTAMP_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.DETECTION_VIOLATORS_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.TOTAL_VIOLATIONS_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.TOTAL_VIOLATORS_KEY
import org.json.JSONArray

class LogsFragment : Fragment() {

    private var _binding: FragmentLogsBinding? = null
    private val binding by lazy { _binding!! }

    private lateinit var detectionAdapter: DetectionAdapter

    companion object {
        lateinit var databaseHelper: DatabaseHelper
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelper = DatabaseHelper(this.requireContext())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentLogsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvDetection = view.findViewById<RecyclerView>(R.id.rvDetection)
//        val btnDeleteAll = view.findViewById<FloatingActionButton>(R.id.btnDeleteAll)

        //set List
        try {
//            val detectionList = databaseHelper.getAllDetection(requireContext())

            val request = RestOptions.builder()
                .addPath("/getLogs")
                .build()
            val detectionList = ArrayList<Detection>()
            Amplify.API.get(request,
                { Log.i("MyAmplifyApp", "GET succeeded: ${it.data.asString()}")
                    val data = JSONArray(it.data.asString())

                    for (i in 0 until data.length()){
                        val detection = Detection()
                        val item = data.getJSONObject(i)
                        detection.id = i
                        detection.image = item.getString("image")
                        detection.cameraName = item.getString("uuid")
                        detection.timestamp = item.getString("timestamp")
                        detection.violators = item.getString("violators")
                        detection.total_violators = item.getString("total_violators")
                        detection.total_violations = item.getString("total_violations")
                        detectionList.add(detection)
                    }
//                    val detections: List<Detection> = detectionList
//                    val totalViolations = Array<Int>(detections.size) { 0 }
//                    var index = 0
//                    for (a in detections) {
//                        totalViolations[index] = a.total_violations.toInt()
//                        index++
//                    }
                    UserData.setLogs(detectionList)



                },
                { Log.e("MyAmplifyApp", "GET failed.", it) }
            )

            UserData.logs.observe(this.viewLifecycleOwner, Observer<ArrayList<Detection>> { logs ->
                // set adapter
                logs.sortByDescending { it.id }
                detectionAdapter = DetectionAdapter(requireContext(), logs)
                //set find Id
                val rvReports: RecyclerView = rvDetection
                //set recycler view adapter
                rvReports.layoutManager = LinearLayoutManager(this.context)
                rvReports.adapter = detectionAdapter

                val adapter = detectionAdapter
                rvReports.adapter = adapter
                adapter.setOnItemClickListener(object : DetectionAdapter.onItemClickListener {
                    override fun onItemClick(position: Int) {
                        val image             = logs[position].image
                        val cameraName        = logs[position].cameraName
                        val timestamp         = logs[position].timestamp
                        val violators         = logs[position].violators
                        val total_violations  = logs[position].total_violations
                        val total_violators   = logs[position].total_violators

                        val detectionBundle = bundleOf(
                            DETECTION_IMAGE_KEY      to image,
                            DETECTION_CAMERA_NAME_KEY to cameraName,
                            DETECTION_TIMESTAMP_KEY  to timestamp,
                            DETECTION_VIOLATORS_KEY  to violators,
                            TOTAL_VIOLATIONS_KEY to total_violations,
                            TOTAL_VIOLATORS_KEY to total_violators)
                        findNavController().navigate(
                            R.id.action_navigation_logs_to_fragment_detection_report, detectionBundle)
                    }
                })
            })


        } catch (e: Exception){
            e.printStackTrace()
        }

//        btnDeleteAll.setOnClickListener { deleteAllDetection() }

    }

//    private fun deleteAllDetection() {
//        val deleteDialog = AlertDialog.Builder(this.requireContext())
//
//        deleteDialog.setTitle("Warning")
//        deleteDialog.setIcon(R.drawable.ic_warning)
//        deleteDialog.setMessage("Are you sure you want to permanently delete all records?")
//        deleteDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, _ ->
//            if (databaseHelper.deleteAllDetection()) {
//                detectionAdapter.notifyDataSetChanged()
//                Toast.makeText(this.requireContext(), "All Records are Deleted", Toast.LENGTH_SHORT)
//                    .show()
//            } else {
//                Toast.makeText(this.requireContext(), "Error Deleting", Toast.LENGTH_SHORT).show()
//            }
//            dialog.dismiss()
//        })
//        deleteDialog.setNegativeButton("Cancel") { dialog, _ ->
//            dialog.dismiss()
//        }
//        deleteDialog.create()
//        deleteDialog.show()
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}