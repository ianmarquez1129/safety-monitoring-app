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
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zmci.safetymonitoringapp.R
import com.zmci.safetymonitoringapp.database.DatabaseHelper
import com.zmci.safetymonitoringapp.databinding.FragmentHomeBinding
import com.zmci.safetymonitoringapp.home.detection.adapter.CameraAdapter
import com.zmci.safetymonitoringapp.home.detection.model.Detection
import com.zmci.safetymonitoringapp.home.detection.utils.CAMERA_NAME_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.MQTT_CLIENT_ID_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.MQTT_TOPIC_KEY
import com.zmci.safetymonitoringapp.logs.LogsFragment

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding by lazy { _binding!! }
    companion object {
        lateinit var databaseHelper: DatabaseHelper
    }
    private lateinit var cameraAdapter: CameraAdapter
    private lateinit var ourLineChart: LineChart

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

        ourLineChart = view.findViewById(R.id.ourLineChart)
        retrieveRecordsAndPopulateCharts()

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

    private fun retrieveRecordsAndPopulateCharts(){
        val detections: List<Detection> = databaseHelper.getAllDetection(requireContext())

        val detectionId = Array<Int>(detections.size){ 0 }
        val totalViolators = Array<Int>(detections.size){ 0 }
        val totalViolations = Array<Int>(detections.size){ 0 }

        var index = 0
        for (a in detections) {
            detectionId[index] = a.id
            totalViolators[index] = a.total_violators.toInt()
            totalViolations[index] = a.total_violations.toInt()
            index++
        }
        populateLineChart(totalViolations)


    }
    private fun populateLineChart(values: Array<Int>) {
        val ourLineChartEntries: ArrayList<Entry> = ArrayList()

        var i = 0

        for (entry in values) {
            var value = values[i].toFloat()
            ourLineChartEntries.add(Entry(i.toFloat(), value))
            i++
        }
        val lineDataSet = LineDataSet(ourLineChartEntries, "")
        lineDataSet.setColors(*ColorTemplate.PASTEL_COLORS)
        val data = LineData(lineDataSet)
        ourLineChart.axisLeft.setDrawGridLines(false)
        val xAxis: XAxis = ourLineChart.xAxis
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        ourLineChart.legend.isEnabled = false

        //remove description label
        ourLineChart.description.isEnabled = false

        //add animation
        ourLineChart.animateX(1000, Easing.EaseInSine)
        ourLineChart.data = data
        //refresh
        ourLineChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}