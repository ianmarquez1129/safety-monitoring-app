package com.zmci.safetymonitoringapp.home.detection

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.zmci.safetymonitoringapp.R
import com.zmci.safetymonitoringapp.database.DatabaseHelper
import com.zmci.safetymonitoringapp.databinding.FragmentClientBinding
import com.zmci.safetymonitoringapp.home.detection.utils.CAMERA_NAME_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.MQTT_CLIENT_ID_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.MQTT_PUB_TOPIC_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.MQTT_SERVER_URI
import com.zmci.safetymonitoringapp.home.detection.utils.MQTT_TOPIC_KEY
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.json.JSONArray
import org.json.JSONObject
import javax.net.ssl.SSLSocketFactory

class ClientFragment : Fragment() {

    private var _binding: FragmentClientBinding? = null
    private val binding by lazy { _binding!! }

    companion object {
        lateinit var databaseHelper: DatabaseHelper
    }

    private lateinit var mqttClient: MQTTClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        databaseHelper = DatabaseHelper(this.requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize views
        val llCameraDetails = view.findViewById<LinearLayout>(R.id.llCameraDetails)
        val llDetails = view.findViewById<LinearLayout>(R.id.llDetails)
        val llDetails2 = view.findViewById<LinearLayout>(R.id.llDetails2)
        val tvTimestamp = view.findViewById<TextView>(R.id.tvTimestamp)
        val textCameraData = view.findViewById<TextView>(R.id.textCameraData)
        val textTotalViolators = view.findViewById<TextView>(R.id.textTotalViolators)
        val textTotalViolations = view.findViewById<TextView>(R.id.textTotalViolations)
        val textDetect = view.findViewById<TextView>(R.id.textDetect)
        val imgDetect = view.findViewById<ImageView>(R.id.imgDetect)
        val cameraClientName = view.findViewById<TextView>(R.id.cameraClientName)

        // Get arguments passed by HomeFragment
        val cameraName = arguments?.getString(CAMERA_NAME_KEY).toString()
        val topic = arguments?.getString(MQTT_TOPIC_KEY).toString()
        val pubTopic = arguments?.getString(MQTT_PUB_TOPIC_KEY).toString()
        val clientId = arguments?.getString(MQTT_CLIENT_ID_KEY).toString()
        val serverURI = MQTT_SERVER_URI

        // Set camera name as title
        cameraClientName.text = "$cameraName/$topic"

        // Open MQTT Broker communication
        mqttClient = MQTTClient(requireContext(), serverURI, clientId)

        Thread {
            Thread.sleep(5000)
            try {
                if (mqttClient.isConnected()) {
                    mqttClient.subscribe(
                        pubTopic,
                        1,
                        object : IMqttActionListener {
                            override fun onSuccess(asyncActionToken: IMqttToken?) {
                                val msg = "Subscribed to: $pubTopic"
                                Log.d(this.javaClass.name, msg)
                            }

                            override fun onFailure(
                                asyncActionToken: IMqttToken?,
                                exception: Throwable?
                            ) {
                                Log.d(this.javaClass.name, "Failed to subscribe: $pubTopic")
                            }
                        })
                } else {
                    Log.d(this.javaClass.name, "Impossible to subscribe, no server connected")
                }

            } catch (e:Exception) {
                e.printStackTrace()
            }
        }.start()

        try {

            if (mqttClient.isConnected()) {
                Log.d(this.javaClass.name, "MQTT is already connected")
                mqttClient.disconnect(object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d(this.javaClass.name, "Disconnected")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(this.javaClass.name, "Failed to disconnect")
                    }
                })

            } else {
                // Connect and login to MQTT Broker
                val options = MqttConnectOptions()
                val sslSocketFactory: SSLSocketFactory? = mqttClient.getSocketFactory(resources.openRawResource(R.raw.amazonrootca1),resources.openRawResource(R.raw.certificate_pem),resources.openRawResource(R.raw.private_pem),"")
                options.socketFactory = sslSocketFactory
                mqttClient.connect(options,
                    object : IMqttActionListener {
                        override fun onSuccess(asyncActionToken: IMqttToken?) {
                            Log.d(this.javaClass.name, "Connection success")
                        }
                        override fun onFailure(
                            asyncActionToken: IMqttToken?,
                            exception: Throwable?
                        ) {
                            Log.d(
                                this.javaClass.name,
                                "Connection failure: ${exception.toString()}"
                            )
                        }
                    },
                    object : MqttCallback {
                        @SuppressLint("SetTextI18n", "MissingPermission")
                        @RequiresApi(Build.VERSION_CODES.O)
                        override fun messageArrived(topic: String?, message: MqttMessage?) {
                            val msg = "Receive message: ${message.toString()} from topic: $topic"
                            Log.d(this.javaClass.name, msg)

                            try {
                                val jsonData = "[ ${message.toString()} ]"
                                val obj = JSONArray(jsonData)
                                val imageObj: JSONObject = obj.getJSONObject(0)

                                // get image in json
                                val imageData = imageObj.getString("image") // get image
                                // get violators in json
                                val violatorsData = imageObj.getString("violators")
                                //get timestamp in json
                                val timestampData = imageObj.getString("timestamp")
                                //total violations
                                val totalViolations = imageObj.getString("total_violations")
                                //total violators
                                val totalViolators = imageObj.getString("total_violators")

                                //violatorsData parse
                                val violatorsObject = JSONArray(violatorsData)

                                try {

                                    // Clear LinearLayout views for the new incoming data
                                    llCameraDetails.removeAllViews()
                                    llDetails.removeAllViews()
                                    llDetails2.removeAllViews()

                                    // Clear TextView after every loop and
                                    // setup a new one for new incoming data
                                    tvTimestamp.text = "Timestamp: $timestampData"
                                    textCameraData.text = "Device details:"
                                    textTotalViolators.text = "Person: $totalViolators"
                                    textTotalViolations.text = "Detected Objects: $totalViolations"
                                    textDetect.text = "Details:"

                                    // Create a TextView for CameraName
                                    val tvCameraDetails = TextView(context)
                                    tvCameraDetails.layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT
                                    )
                                    tvCameraDetails.textSize = 20f
                                    tvCameraDetails.typeface = Typeface.DEFAULT_BOLD
                                    tvCameraDetails.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
                                    tvCameraDetails.text =
                                        "Device Name: $cameraName\n"
                                    // Display the TextView in LinearLayout view
                                    llCameraDetails.addView(tvCameraDetails)

                                    //decode base64 to image
                                    val decodedByte = Base64.decode(imageData, Base64.DEFAULT)
                                    val bitmap =
                                        BitmapFactory.decodeByteArray(
                                            decodedByte,
                                            0,
                                            decodedByte.size
                                        )
                                    //set image in imageview
                                    imgDetect.setImageBitmap(bitmap)

                                    //violators extract
                                    for (i in 0 until violatorsObject.length()) {
                                        val item = violatorsObject.getJSONObject(i)
//                                        val itemPersonInfo = item.getString("person_info")
//                                        // extract itemPersonInfo
//                                        val itemPersonInfoObject = JSONArray(itemPersonInfo)
//
//                                        // If the current person has no 'face' and is not recognized,
//                                        // it is considered "Unknown person".
//                                        if (itemPersonInfoObject.length() == 0) {
//                                            // Create TextView for "Unknown person"
//                                            val tvPerson = TextView(context)
//                                            tvPerson.layoutParams = ViewGroup.LayoutParams(
//                                                ViewGroup.LayoutParams.MATCH_PARENT,
//                                                ViewGroup.LayoutParams.WRAP_CONTENT
//                                            )
//                                            tvPerson.textSize = 20f
//                                            tvPerson.typeface = Typeface.DEFAULT_BOLD
//                                            tvPerson.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
//                                            tvPerson.text = "Unknown person"
//                                            // Display the TextView in LinearLayout view
//                                            if (i % 2 == 0) {
//                                                llDetails.addView(tvPerson)
//                                            } else {
//                                                llDetails2.addView(tvPerson)
//                                            }
//                                        }
//
//                                        // If the current person has a detected 'face'...
//                                        for (j in 0 until itemPersonInfoObject.length()) {
//                                            val itemLength = itemPersonInfoObject.getJSONObject(j).length()
//                                            // If the person has been recognized via face recognition
//                                            if (itemLength > 1) {
//                                                val itemPI = itemPersonInfoObject.getJSONObject(j)
//                                                val personId = itemPI.getString("person_id")
//                                                val firstName = itemPI.getString("first_name")
//                                                val middleName = itemPI.getString("middle_name")
//                                                val lastName = itemPI.getString("last_name")
//                                                val jobTitle = itemPI.getString("job_title")
//                                                val overlaps = itemPI.getString("overlaps")
//                                                val tvPersonInfo = TextView(context)
//                                                tvPersonInfo.layoutParams = ViewGroup.LayoutParams(
//                                                    ViewGroup.LayoutParams.MATCH_PARENT,
//                                                    ViewGroup.LayoutParams.WRAP_CONTENT
//                                                )
//                                                tvPersonInfo.textSize = 20f
//                                                tvPersonInfo.typeface = Typeface.DEFAULT_BOLD
//                                                tvPersonInfo.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
//                                                tvPersonInfo.text =
//                                                    "ID: $personId\n" +
//                                                            "Name: $firstName $middleName $lastName\n" +
//                                                            "Job Title: $jobTitle\n" +
//                                                            "Overlaps: $overlaps"
//                                                if (i % 2 == 0) {
//                                                    llDetails.addView(tvPersonInfo)
//                                                } else {
//                                                    llDetails2.addView(tvPersonInfo)
//                                                }
//
//                                            }
//                                            // If the person is not recognized via 'face recognition'
//                                            // the person is considered "Unknown"
//                                            else {
//                                                val itemPI = itemPersonInfoObject.getJSONObject(j)
//                                                val overlaps = itemPI.getString("overlaps")
//                                                // Create TextView for "Unknown person"
//                                                val tvPersonUnknown = TextView(context)
//                                                tvPersonUnknown.layoutParams =
//                                                    ViewGroup.LayoutParams(
//                                                        ViewGroup.LayoutParams.MATCH_PARENT,
//                                                        ViewGroup.LayoutParams.WRAP_CONTENT
//                                                    )
//                                                tvPersonUnknown.textSize = 20f
//                                                tvPersonUnknown.typeface = Typeface.DEFAULT_BOLD
//                                                tvPersonUnknown.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
//                                                tvPersonUnknown.text =
//                                                    "Unknown person\n" +
//                                                            "Overlaps: $overlaps"
//                                                // Display the TextView in LinearLayout view
//                                                if (i % 2 == 0) {
//                                                    llDetails.addView(tvPersonUnknown)
//                                                } else {
//                                                    llDetails2.addView(tvPersonUnknown)
//                                                }
//
//                                            }
//
//                                        }

                                        // Set the person's ID, and detections
                                        val personUniqueID = item.getString("id")
                                        val itemViolations = item.getString("violations")
                                        val itemViolationsObject = JSONArray(itemViolations)
                                        val tvPersonID = TextView(context)
                                        // Create a TextView for person's ID and detections
                                        tvPersonID.layoutParams = ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                        )
                                        tvPersonID.textSize = 20f
                                        tvPersonID.typeface = Typeface.DEFAULT_BOLD
                                        tvPersonID.setTextColor(ContextCompat.getColor(requireContext(),R.color.black))
                                        tvPersonID.text = "\nPerson ID: $personUniqueID \n\nDetections:"
                                        // Display the TextView in LinearLayout view
                                        if (i % 2 == 0) {
                                            llDetails.addView(tvPersonID)
                                        } else {
                                            llDetails2.addView(tvPersonID)
                                        }

                                        try {
                                            // Processing of detected PPE with their corresponding color coding
                                            for (k in 0 until itemViolationsObject.length()) {
                                                // Obtain the detections from JSON
                                                val itemV = itemViolationsObject.getJSONObject(k)
                                                val className = itemV.getString("class_name")
                                                // Create TextView for each detections
                                                val tvPPE = TextView(context)
                                                tvPPE.layoutParams = ViewGroup.LayoutParams(
                                                    ViewGroup.LayoutParams.MATCH_PARENT,
                                                    ViewGroup.LayoutParams.WRAP_CONTENT
                                                )
                                                tvPPE.textSize = 20f
                                                tvPPE.typeface = Typeface.DEFAULT_BOLD
                                                tvPPE.text = className
                                                // Setting the color coding of each PPE detections
                                                try {
                                                    when (className) {
                                                        "helmet" -> {
                                                            tvPPE.setTextColor(
                                                                ContextCompat.getColor(
                                                                    requireContext(),
                                                                    R.color.helmet
                                                                )
                                                            )
                                                        }

                                                        "no helmet" -> {
                                                            tvPPE.setTextColor(
                                                                ContextCompat.getColor(
                                                                    requireContext(),
                                                                    R.color.no_helmet
                                                                )
                                                            )
                                                        }

                                                        "glasses" -> {
                                                            tvPPE.setTextColor(
                                                                ContextCompat.getColor(
                                                                    requireContext(),
                                                                    R.color.glasses
                                                                )
                                                            )
                                                        }

                                                        "no glasses" -> {
                                                            tvPPE.setTextColor(
                                                                ContextCompat.getColor(
                                                                    requireContext(),
                                                                    R.color.no_glasses
                                                                )
                                                            )
                                                        }

                                                        "vest" -> {
                                                            tvPPE.setTextColor(
                                                                ContextCompat.getColor(
                                                                    requireContext(),
                                                                    R.color.vest
                                                                )
                                                            )
                                                        }

                                                        "no vest" -> {
                                                            tvPPE.setTextColor(
                                                                ContextCompat.getColor(
                                                                    requireContext(),
                                                                    R.color.no_vest
                                                                )
                                                            )
                                                        }

                                                        "gloves" -> {
                                                            tvPPE.setTextColor(
                                                                ContextCompat.getColor(
                                                                    requireContext(),
                                                                    R.color.gloves
                                                                )
                                                            )
                                                        }

                                                        "no gloves" -> {
                                                            tvPPE.setTextColor(
                                                                ContextCompat.getColor(
                                                                    requireContext(),
                                                                    R.color.no_gloves
                                                                )
                                                            )
                                                        }

                                                        "boots" -> {
                                                            tvPPE.setTextColor(
                                                                ContextCompat.getColor(
                                                                    requireContext(),
                                                                    R.color.boots
                                                                )
                                                            )
                                                        }

                                                        "no boots" -> {
                                                            tvPPE.setTextColor(
                                                                ContextCompat.getColor(
                                                                    requireContext(),
                                                                    R.color.no_boots
                                                                )
                                                            )
                                                        }
                                                    }
                                                    // Display the TextView in LinearLayout view
                                                    if (i % 2 == 0) {
                                                        llDetails.addView(tvPPE)
                                                    } else {
                                                        llDetails2.addView(tvPPE)
                                                    }
                                                } catch (e: Exception) {
                                                    e.printStackTrace()
                                                }
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                        // Horizontal line separator
                                        val tvLine = TextView(context)
                                        tvLine.layoutParams = ViewGroup.LayoutParams(
                                            ViewGroup.LayoutParams.MATCH_PARENT,
                                            10
                                        )
                                        tvLine.setBackgroundColor(
                                            ContextCompat.getColor(
                                                requireContext(),
                                                R.color.black
                                            ))
                                        // Display of Horizontal Line in LinearLayout view
                                        if (i % 2 == 0) {
                                            llDetails.addView(tvLine)
                                        } else {
                                            llDetails2.addView(tvLine)
                                        }
                                    }
                                } catch (e : Exception) {
                                    e.printStackTrace()
                                }
                                //Save data to database
//                                val detectionDB = Detection()
//                                detectionDB.image = imageData
//                                detectionDB.cameraName = cameraName
//                                detectionDB.timestamp = timestampData
//                                detectionDB.violators = violatorsData
//                                detectionDB.total_violations = totalViolations
//                                detectionDB.total_violators = totalViolators
//                                databaseHelper.addDetection(context, detectionDB)
                            } catch (e : Exception) {
                                e.printStackTrace()
                            }

//                            //notification
//                            Thread {
//                                try {
////                                    createNotificationChannel2()
//                                    val intentNotify = Intent(context, MainActivity::class.java)
//                                    val pendingIntent = TaskStackBuilder.create(context).run {
//                                        addNextIntentWithParentStack(intentNotify)
//                                        getPendingIntent(0, PendingIntent.FLAG_MUTABLE)
//                                    }
//                                    val notification =
//                                        NotificationCompat.Builder(requireContext(), CHANNEL_ID2)
//                                            .setContentTitle("Detection alert")
//                                            .setContentText("Review logs for details")
//                                            .setSmallIcon(R.drawable.ic_detect)
//                                            .setPriority(NotificationCompat.PRIORITY_HIGH)
//                                            .setContentIntent(pendingIntent)
//                                            .build()
//                                    val notificationManager =
//                                        NotificationManagerCompat.from(requireContext())
//                                    notificationManager.notify(NOTIFICATION_ID2, notification)
//                                } catch (e: Exception){
//                                    e.printStackTrace()
//                                }
//                            }.start()

                        }

                        override fun connectionLost(cause: Throwable?) {
                            Log.d(this.javaClass.name, "Connection lost ${cause.toString()}")
                        }

                        override fun deliveryComplete(token: IMqttDeliveryToken?) {
                            Log.d(this.javaClass.name, "Delivery complete")
                        }
                    })
            }
        } catch (e:Exception) {
            e.printStackTrace()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()

        try {
            if (mqttClient.isConnected()) {
                // Disconnect from MQTT Broker
                mqttClient.disconnect(object : IMqttActionListener {
                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                        Log.d(this.javaClass.name, "Disconnected")
                    }

                    override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                        Log.d(this.javaClass.name, "Failed to disconnect")
                    }
                })
            } else {
                Log.d(this.javaClass.name, "Impossible to disconnect, no server connected")
            }
        } catch (e: Exception){
            e.printStackTrace()
        }

        _binding = null
    }

}