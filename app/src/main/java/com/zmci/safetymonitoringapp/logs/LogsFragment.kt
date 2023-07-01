package com.zmci.safetymonitoringapp.logs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zmci.safetymonitoringapp.R
import com.zmci.safetymonitoringapp.database.DatabaseHelper
import com.zmci.safetymonitoringapp.databinding.FragmentLogsBinding
import com.zmci.safetymonitoringapp.home.detection.adapter.DetectionAdapter
import com.zmci.safetymonitoringapp.home.detection.utils.DETECTION_CAMERA_NAME_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.DETECTION_IMAGE_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.DETECTION_TIMESTAMP_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.DETECTION_VIOLATORS_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.TOTAL_VIOLATIONS_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.TOTAL_VIOLATORS_KEY

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
        val btnDeleteAll = view.findViewById<FloatingActionButton>(R.id.btnDeleteAll)

        //set List
        try {
            val detectionList = databaseHelper.getAllDetection(requireContext())
            // set adapter
            detectionList.sortByDescending { it.id }
            detectionAdapter = DetectionAdapter(requireContext(), detectionList)
            //set find Id
            val rvReports: RecyclerView = rvDetection
            //set recycler view adapter
            rvReports.layoutManager = LinearLayoutManager(this.context)
            rvReports.adapter = detectionAdapter

            val adapter = detectionAdapter
            rvReports.adapter = adapter
            adapter.setOnItemClickListener(object : DetectionAdapter.onItemClickListener {
                override fun onItemClick(position: Int) {
                    val image             = detectionList[position].image
                    val cameraName        = detectionList[position].cameraName
                    val timestamp         = detectionList[position].timestamp
                    val violators         = detectionList[position].violators
                    val total_violations  = detectionList[position].total_violations
                    val total_violators   = detectionList[position].total_violators

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
        } catch (e: Exception){
            e.printStackTrace()
        }

        btnDeleteAll.setOnClickListener { deleteAllDetection() }

    }

    private fun deleteAllDetection() {
        val deleteDialog = AlertDialog.Builder(this.requireContext())

        deleteDialog.setTitle("Warning")
        deleteDialog.setIcon(R.drawable.ic_warning)
        deleteDialog.setMessage("Are you sure you want to permanently delete all records?")
        deleteDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, _ ->
            if (databaseHelper.deleteAllDetection()) {
                detectionAdapter.notifyDataSetChanged()
                Toast.makeText(this.requireContext(), "All Records are Deleted", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(this.requireContext(), "Error Deleting", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        })
        deleteDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        deleteDialog.create()
        deleteDialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}