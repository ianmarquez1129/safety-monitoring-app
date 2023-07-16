package com.zmci.safetymonitoringapp.home.detection

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
import com.zmci.safetymonitoringapp.databinding.FragmentCameraLogsBinding
import com.zmci.safetymonitoringapp.home.detection.adapter.DetectionAdapter
import com.zmci.safetymonitoringapp.home.detection.model.Detection
import com.zmci.safetymonitoringapp.home.detection.utils.CAMERA_NAME_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.DETECTION_CAMERA_NAME_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.DETECTION_IMAGE_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.DETECTION_TIMESTAMP_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.DETECTION_VIOLATORS_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.MQTT_TOPIC_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.TOTAL_VIOLATIONS_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.TOTAL_VIOLATORS_KEY
import org.json.JSONArray
import java.util.Calendar

class CameraLogsFragment : Fragment() {

    private var _binding: FragmentCameraLogsBinding? = null
    private val binding by lazy { _binding!! }

    private lateinit var detectionAdapter: DetectionAdapter

    private lateinit var startDatetime : TextView
    private lateinit var endDatetime : TextView

    companion object {
        lateinit var databaseHelper: DatabaseHelper
    }

    var day = 0
    var month = 0
    var year = 0
    var hour = 0
    var minute = 0

    var savedDayStart = 0
    var savedMonthStart = 0
    var savedYearStart = 0
    var savedHourStart = 0
    var savedMinuteStart = 0
    var savedDayEnd = 0
    var savedMonthEnd = 0
    var savedYearEnd = 0
    var savedHourEnd = 0
    var savedMinuteEnd = 0

    var savedMonthStartPad = ""
    var savedDayStartPad = ""
    var savedHourStartPad = ""
    var savedMinuteStartPad = ""
    var savedMonthEndPad = ""
    var savedDayEndPad = ""
    var savedHourEndPad = ""
    var savedMinuteEndPad = ""

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
        startDatetime = view.findViewById(R.id.startDatetime)
        endDatetime = view.findViewById(R.id.endDatetime)
        val datetimeSubmit = view.findViewById<Button>(R.id.datetimeSubmit)


        // Get arguments passed by HomeFragment
        val cameraName = arguments?.getString(CAMERA_NAME_KEY).toString()
        val topic = arguments?.getString(MQTT_TOPIC_KEY).toString()

        // set camera name as title
        cameraLogs.text = "$topic Logs"

        startDatetime.setOnClickListener {
            getDateTimeCalendar()
            val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, month, day ->
                savedYearStart = year
                savedMonthStart = month+1
                savedDayStart = day
                savedMonthStartPad = savedMonthStart.toString().padStart(2,'0')
                savedDayStartPad = savedDayStart.toString().padStart(2,'0')
            }, year, month, day)
            val tpd = TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                savedHourStart = hour
                savedMinuteStart = minute
                savedHourStartPad = savedHourStart.toString().padStart(2,'0')
                savedMinuteStartPad = savedMinuteStart.toString().padStart(2,'0')
                startDatetime.text = "$savedYearStart-$savedMonthStartPad-$savedDayStartPad $savedHourStartPad:$savedMinuteStartPad:00"
            },hour,minute,true)
            tpd.show()
            dpd.show()
        }
        endDatetime.setOnClickListener {
            getDateTimeCalendar()
            getDateTimeCalendar()
            val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, month, day ->
                savedYearEnd = year
                savedMonthEnd = month+1
                savedDayEnd = day
                savedMonthEndPad = savedMonthEnd.toString().padStart(2,'0')
                savedDayEndPad = savedDayEnd.toString().padStart(2,'0')
            }, year, month, day)
            val tpd = TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                savedHourEnd = hour
                savedMinuteEnd = minute
                savedHourEndPad = savedHourEnd.toString().padStart(2,'0')
                savedMinuteEndPad = savedMinuteEnd.toString().padStart(2,'0')
                endDatetime.text = "$savedYearEnd-$savedMonthEndPad-$savedDayEndPad $savedHourEndPad:$savedMinuteEndPad:00"
            },hour,minute,true)
            tpd.show()
            dpd.show()
        }

        datetimeSubmit.setOnClickListener {
            try {
                val options = RestOptions.builder()
                    .addPath("/getLogs")
                    .addBody(("{" +
                        "\"uuid\":\"$topic\"," +
                        "\"start_datetime\":\"$savedYearStart-$savedMonthStartPad-$savedDayStartPad" + " " + "$savedHourStartPad:$savedMinuteStartPad:00\"," +
                        "\"end_datetime\":\"$savedYearEnd-$savedMonthEndPad-$savedDayEndPad" + " " + "$savedHourEndPad:$savedMinuteEndPad:00\"" +
                        "}").encodeToByteArray())
                    .build()
                Log.i("getLogs", "{" +
                        "\"uuid\":\"$topic\"," +
                        "\"start_datetime\":\"$savedYearStart-$savedMonthStartPad-$savedDayStartPad" + " " + "$savedHourStartPad:$savedMinuteStartPad:00\"," +
                        "\"end_datetime\":\"$savedYearEnd-$savedMonthEndPad-$savedDayEndPad" + " " + "$savedHourEndPad:$savedMinuteEndPad:00\"" +
                        "}")
                val detectionList = ArrayList<Detection>()
                Amplify.API.post(options,
                    { Log.i("MyAmplifyApp", "POST succeeded: ${it.data.asString()}")
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
                        UserData.setDeviceLogs(detectionList)
                    },
                    { Log.e("MyAmplifyApp", "POST failed", it) }
                )

                UserData.deviceLogs.observe(this.viewLifecycleOwner, Observer<ArrayList<Detection>> { deviceLogs ->
                    deviceLogs.sortByDescending { it.id }
                    detectionAdapter = DetectionAdapter(requireContext(), deviceLogs)
                    //set find Id
                    val rvReports: RecyclerView = rvCameraDetection
                    //set recycler view adapter
                    rvReports.layoutManager = LinearLayoutManager(this.context)
                    rvReports.adapter = detectionAdapter

                    val adapter = detectionAdapter
                    rvReports.adapter = adapter
                    adapter.setOnItemClickListener(object : DetectionAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            val image             = deviceLogs[position].image
                            val cameraName2        = deviceLogs[position].cameraName
                            val timestamp         = deviceLogs[position].timestamp
                            val violators         = deviceLogs[position].violators
                            val total_violations  = deviceLogs[position].total_violations
                            val total_violators   = deviceLogs[position].total_violators

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

                })

            } catch (e:Exception){
                e.printStackTrace()
            }
        }
    }

    private fun getDateTimeCalendar() {
        val cal : Calendar = Calendar.getInstance()
        day = cal.get(Calendar.DAY_OF_MONTH)
        month = cal.get(Calendar.MONTH)
        year = cal.get(Calendar.YEAR)
        hour = cal.get(Calendar.HOUR)
        minute = cal.get(Calendar.MINUTE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}