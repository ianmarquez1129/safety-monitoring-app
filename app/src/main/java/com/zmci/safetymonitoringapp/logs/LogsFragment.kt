package com.zmci.safetymonitoringapp.logs

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.text.InputFilter
import android.text.Spanned
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amplifyframework.api.rest.RestOptions
import com.amplifyframework.core.Amplify
import com.google.android.material.snackbar.Snackbar
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
import java.util.Calendar

class LogsFragment : Fragment() {

    private var _binding: FragmentLogsBinding? = null
    private val binding by lazy { _binding!! }

    private lateinit var detectionAdapter: DetectionAdapter

    companion object {
        lateinit var databaseHelper: DatabaseHelper
    }

    private lateinit var startDatetime : TextView

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

    var savedYearStartPad = "2023"
    var savedMonthStartPad = "01"
    var savedDayStartPad = "01"
    var savedHourStartPad = "00"
    var savedMinuteStartPad = "00"

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
        startDatetime = view.findViewById(R.id.startDatetime)
        val datetimeSubmit = view.findViewById<Button>(R.id.datetimeSubmit)
        val numberEntries = view.findViewById<EditText>(R.id.numberEntries)

        numberEntries.filters = arrayOf<InputFilter>(MinMaxFilter(1,100))

        startDatetime.setOnClickListener {
            getDateTimeCalendar()
            val dpd = DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { view, year, month, day ->
                savedYearStart = year
                savedMonthStart = month+1
                savedDayStart = day
                savedYearStartPad = savedYearStart.toString().padStart(4,'0')
                savedMonthStartPad = savedMonthStart.toString().padStart(2,'0')
                savedDayStartPad = savedDayStart.toString().padStart(2,'0')
            }, year, month, day)
            val tpd = TimePickerDialog(requireContext(), TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                savedHourStart = hour
                savedMinuteStart = minute
                savedHourStartPad = savedHourStart.toString().padStart(2,'0')
                savedMinuteStartPad = savedMinuteStart.toString().padStart(2,'0')
                startDatetime.setTextColor(resources.getColor(R.color.warm_black))
                startDatetime.text = "$savedYearStartPad-$savedMonthStartPad-$savedDayStartPad $savedHourStartPad:$savedMinuteStartPad:00"
            },hour,minute,true)
            tpd.show()
            dpd.show()
        }

        datetimeSubmit.setOnClickListener {
            if (numberEntries.text.toString().isEmpty()) {
                val snackBarView = Snackbar.make(binding.root, R.string.fill_out_empty_fields , Snackbar.LENGTH_LONG)
                val view2 = snackBarView.view
                val params = view2.layoutParams as FrameLayout.LayoutParams
                params.gravity = Gravity.TOP
                view2.layoutParams = params
                snackBarView.setBackgroundTint(Color.RED)
                snackBarView.show()
            } else {
                Toast.makeText(context,"Success", Toast.LENGTH_LONG).show()
                //set List
                try {
                    val request = RestOptions.builder()
                        .addPath("/getOverallLogs")
                        .addBody(
                            ("{" +
                                    "\"start_datetime\":\"$savedYearStartPad-$savedMonthStartPad-$savedDayStartPad" + " " + "$savedHourStartPad:$savedMinuteStartPad:00\"," +
                                    "\"number_of_logs\":\"${numberEntries.text}\"" +
                                    "}").encodeToByteArray()
                        )
                        .build()
                    Log.i(
                        "getOverallLogs", "{" +
                                "\"start_datetime\":\"$savedYearStartPad-$savedMonthStartPad-$savedDayStartPad" + " " + "$savedHourStartPad:$savedMinuteStartPad:00\"," +
                                "\"number_of_logs\":\"${numberEntries.text}\"" +
                                "}"
                    )
                    val detectionList = ArrayList<Detection>()
                    Amplify.API.post(request,
                        { Log.i("getOverallLogs", "POST succeeded: ${it.data.asString()}")
                            try {
                                val data = JSONArray(it.data.asString())

                                for (i in 0 until data.length()) {
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
                                UserData.setLogs(detectionList)
                            } catch (e:Exception){
                                e.printStackTrace()
                            }
                        },
                        { Log.e("getOverallLogs", "POST failed.", it) }
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
            }
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

    inner class MinMaxFilter() : InputFilter {
        private var intMin: Int = 0
        private var intMax: Int = 0

        // Initialized
        constructor(minValue: Int, maxValue: Int) : this() {
            this.intMin = minValue
            this.intMax = maxValue
        }

        override fun filter(source: CharSequence, start: Int, end: Int, dest: Spanned, dStart: Int, dEnd: Int): CharSequence? {
            try {
                val input = Integer.parseInt(dest.toString() + source.toString())
                if (isInRange(intMin, intMax, input)) {
                    return null
                }
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            return ""
        }

        // Check if input c is in between min a and max b and
        // returns corresponding boolean
        private fun isInRange(a: Int, b: Int, c: Int): Boolean {
            return if (b > a) c in a..b else c in b..a
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