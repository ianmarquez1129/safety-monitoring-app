package com.zmci.safetymonitoringapp.home

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.core.Amplify
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.zmci.safetymonitoringapp.R
import com.zmci.safetymonitoringapp.UserData
import com.zmci.safetymonitoringapp.database.DatabaseHelper
import com.zmci.safetymonitoringapp.databinding.FragmentHomeBinding
import com.zmci.safetymonitoringapp.home.detection.adapter.CameraAdapter
import com.zmci.safetymonitoringapp.home.detection.model.Detection
import com.zmci.safetymonitoringapp.home.detection.utils.CAMERA_NAME_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.MQTT_CLIENT_ID_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.MQTT_PUB_TOPIC_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.MQTT_TOPIC_KEY
import org.json.JSONArray
import java.io.File
import java.io.FileOutputStream

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding by lazy { _binding!! }
    companion object {
        lateinit var databaseHelper: DatabaseHelper
        @SuppressLint("StaticFieldLeak")
        lateinit var cameraAdapter: CameraAdapter
        @SuppressLint("StaticFieldLeak")
        lateinit var numberDevices: TextView
    }

    private lateinit var ourLineChart: LineChart

    private val STORAGE_REQUEST_CODE_EXPORT = 1
    private lateinit var storagePermission:Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelper = DatabaseHelper(this.requireContext())
        storagePermission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
        val generatePDF = view.findViewById<FloatingActionButton>(R.id.generatePDF)

        generatePDF.setOnClickListener {
            exportPDF()
        }

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
                    val pubTopic = cameraList[position].MQTT_PUB_TOPIC
                    val clientID = cameraList[position].MQTT_CLIENT_ID

                    val mqttCredentialsBundle = bundleOf(
                        CAMERA_NAME_KEY to cameraName,
                        MQTT_TOPIC_KEY to topic,
                        MQTT_PUB_TOPIC_KEY to pubTopic,
                        MQTT_CLIENT_ID_KEY to clientID
                    )
                    findNavController().navigate(
                        R.id.action_navigation_home_to_fragment_client, mqttCredentialsBundle
                    )
                }
            })
            numberDevices = view.findViewById(R.id.numberDevices)
            numberDevices.text = "Devices: ${adapter.itemCount}"
        } catch (e : Exception) {
            e.printStackTrace()
        }

    }

    private fun exportPDF() {

        val exportDialog = AlertDialog.Builder(this.requireContext())

        exportDialog.setTitle("Generate PDF")
        exportDialog.setMessage("Are you sure you want to generate PDF?")
        exportDialog.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, _ ->
            if (checkStoragePermission()){
                // permission allowed, do export
                exportPDFProcess()
            } else {
                requestStoragePermissionExport()
            }
            dialog.dismiss()
        })
        exportDialog.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        exportDialog.create()
        exportDialog.show()
    }

    private fun exportPDFProcess() {
        val request = RestOptions.builder()
            .addPath("/getPDF")
            .build()

        Amplify.API.get(request,
            { Log.i("MyAmplifyApp", "GET succeeded: ${it.data.asString()}")
                // Decode the base64 response
//                val decodedBytes: ByteArray = Base64.decode(it.data.asString(), Base64.DEFAULT)
                generatePDFFromBase64(it.data.asString(),"OUTPUT")
            },
            { Log.e("MyAmplifyApp", "GET failed.", it) }
        )
    }
    private fun checkStoragePermission():Boolean {
        return ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED)
    }
    private fun requestStoragePermissionExport(){
        ActivityCompat.requestPermissions(requireActivity(),storagePermission,STORAGE_REQUEST_CODE_EXPORT)
    }
    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // handle permission result
        super.onRequestPermissionsResult(requestCode,permissions,grantResults)

        when(requestCode){
            STORAGE_REQUEST_CODE_EXPORT -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    exportPDFProcess()
                } else {
                    Toast.makeText(this.requireContext(),"Permission denied...", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    fun generatePDFFromBase64(base64: String, fileName: String) {
        try {
            val decodedBytes: ByteArray = Base64.decode(base64, Base64.DEFAULT)
            val fos = FileOutputStream(getFilePath(fileName))
            fos.write(decodedBytes)
            fos.flush()
            fos.close()

        } catch (e: Exception) {
            Log.e("TAG", "Failed to generate pdf from base64: ${e.localizedMessage}")
        }
    }


    fun getFilePath(filename: String): String {
        val file =
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath.toString() + "/" + filename + ".pdf"
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
        val request = RestOptions.builder()
            .addPath("/getLogs")
            .build()
        val detectionList = ArrayList<Detection>()
        Amplify.API.get(request,
            { Log.i("getLogs", "GET succeeded: ${it.data.asString()}")
                try {
                    val data = JSONArray(it.data.asString())
                    for (i in 0 until data.length()) {
                        val detection = Detection()
                        val item = data.getJSONObject(i)
                        detection.id = i
                        detection.image = "image"
                        detection.cameraName = "uuid"
                        detection.timestamp = "timestamp"
                        detection.violators = "violators"
                        detection.total_violators = "total_violators"
                        detection.total_violations = item.getString("total_violations")
                        detectionList.add(detection)
                    }
                    val detections: List<Detection> = detectionList
                    val totalViolations = Array<Int>(detections.size) { 0 }
                    var index = 0
                    for (a in detections) {
                        totalViolations[index] = a.total_violations.toInt()
                        index++
                    }
                    UserData.setChart(totalViolations)
                } catch (e:Exception) {
                    e.printStackTrace()
                }

            },
            { Log.e("getLogs", "GET failed.", it) }
        )

        UserData.chart.observe(this.viewLifecycleOwner, Observer<Array<Int>> { chart ->
            populateLineChart(chart)
            Log.i("retrieveRecords", chart.contentDeepToString())
        })


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