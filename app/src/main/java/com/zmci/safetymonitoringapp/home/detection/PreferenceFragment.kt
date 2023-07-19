package com.zmci.safetymonitoringapp.home.detection

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.kusu.loadingbutton.LoadingButton
import com.zmci.safetymonitoringapp.R
import com.zmci.safetymonitoringapp.databinding.FragmentPreferenceBinding
import com.zmci.safetymonitoringapp.home.detection.utils.CAMERA_NAME_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.MQTT_CLIENT_ID_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.MQTT_SERVER_URI
import com.zmci.safetymonitoringapp.home.detection.utils.MQTT_SET_TOPIC_KEY
import com.zmci.safetymonitoringapp.home.detection.utils.MQTT_TOPIC_KEY
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.json.JSONObject
import javax.net.ssl.SSLSocketFactory

class PreferenceFragment : Fragment() {

    private var _binding: FragmentPreferenceBinding? = null
    private val binding by lazy { _binding!! }

    private lateinit var mqttClient: MQTTClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentPreferenceBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val helmetGroup = view.findViewById<RadioGroup>(R.id.helmetGroup)
        val glassesGroup = view.findViewById<RadioGroup>(R.id.glassesGroup)
        val vestGroup = view.findViewById<RadioGroup>(R.id.vestGroup)
        val glovesGroup = view.findViewById<RadioGroup>(R.id.glovesGroup)
        val bootsGroup = view.findViewById<RadioGroup>(R.id.bootsGroup)
        val buttonSetPreferences = view.findViewById<LoadingButton>(R.id.buttonSetPreferences)
        val preferenceTitle = view.findViewById<TextView>(R.id.preferenceTitle)

        // Get arguments passed by HomeFragment
        val cameraName = arguments?.getString(CAMERA_NAME_KEY).toString()
        val topic = arguments?.getString(MQTT_TOPIC_KEY).toString()
        val setTopic = arguments?.getString(MQTT_SET_TOPIC_KEY).toString()
        val clientId = arguments?.getString(MQTT_CLIENT_ID_KEY).toString()
        val serverURI = MQTT_SERVER_URI

        preferenceTitle.text = "Setting Preference for device: $topic"

        // Open MQTT Broker communication
        mqttClient = MQTTClient(requireContext(), serverURI, clientId)

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
                    })
            }
        } catch (e : Exception){
            e.printStackTrace()
        }

        val ppePreferences = HashMap<String, Boolean>()
        ppePreferences["helmet"] = true
        ppePreferences["no_helmet"] = true
        ppePreferences["glasses"] = true
        ppePreferences["no_glasses"] = true
        ppePreferences["vest"] = true
        ppePreferences["no_vest"] = true
        ppePreferences["gloves"] = true
        ppePreferences["no_gloves"] = true
        ppePreferences["boots"] = true
        ppePreferences["no_boots"] = true


        buttonSetPreferences.setOnClickListener {
            val idHelmet : Int = helmetGroup.checkedRadioButtonId
            val idGlasses : Int = glassesGroup.checkedRadioButtonId
            val idVest : Int = vestGroup.checkedRadioButtonId
            val idGloves : Int = glovesGroup.checkedRadioButtonId
            val idBoots : Int = bootsGroup.checkedRadioButtonId
            val radioHelmet : RadioButton = view.findViewById(idHelmet)
            val radioGlasses : RadioButton = view.findViewById(idGlasses)
            val radioVest : RadioButton = view.findViewById(idVest)
            val radioGloves : RadioButton = view.findViewById(idGloves)
            val radioBoots : RadioButton = view.findViewById(idBoots)
            when (idHelmet!=-1 && idGlasses!=-1 && idVest!=-1 && idGloves!=-1 && idBoots!=-1) {
                true -> {
                    Log.i("Helmet", radioHelmet.text.toString())
                    Log.i("Glasses", radioGlasses.text.toString())
                    Log.i("Vest", radioVest.text.toString())
                    Log.i("Gloves", radioGloves.text.toString())
                    Log.i("Boots", radioBoots.text.toString())

                    when(radioHelmet.text) {
                        "Both Helmet" -> {
                            ppePreferences["helmet"] = true
                            ppePreferences["no_helmet"] = true
                        }
                        "No Helmet" -> {
                            ppePreferences["helmet"] = false
                            ppePreferences["no_helmet"] = false
                        }
                        "With Helmet" -> {
                            ppePreferences["helmet"] = true
                            ppePreferences["no_helmet"] = false
                        }
                        "Without Helmet" -> {
                            ppePreferences["helmet"] = false
                            ppePreferences["no_helmet"] = true
                        }
                    }
                    when(radioGlasses.text) {
                        "Both Glasses" -> {
                            ppePreferences["glasses"] = true
                            ppePreferences["no_glasses"] = true
                        }
                        "No Glasses" -> {
                            ppePreferences["glasses"] = false
                            ppePreferences["no_glasses"] = false
                        }
                        "With Glasses" -> {
                            ppePreferences["glasses"] = true
                            ppePreferences["no_glasses"] = false
                        }
                        "Without Glasses" -> {
                            ppePreferences["glasses"] = false
                            ppePreferences["no_glasses"] = true
                        }
                    }
                    when(radioVest.text) {
                        "Both Vest" -> {
                            ppePreferences["vest"] = true
                            ppePreferences["no_vest"] = true
                        }
                        "No Vest" -> {
                            ppePreferences["vest"] = false
                            ppePreferences["no_vest"] = false
                        }
                        "With Vest" -> {
                            ppePreferences["vest"] = true
                            ppePreferences["no_vest"] = false
                        }
                        "Without Vest" -> {
                            ppePreferences["vest"] = false
                            ppePreferences["no_vest"] = true
                        }
                    }
                    when(radioGloves.text) {
                        "Both Gloves" -> {
                            ppePreferences["gloves"] = true
                            ppePreferences["no_gloves"] = true
                        }
                        "No Gloves" -> {
                            ppePreferences["gloves"] = false
                            ppePreferences["no_gloves"] = false
                        }
                        "With Gloves" -> {
                            ppePreferences["gloves"] = true
                            ppePreferences["no_gloves"] = false
                        }
                        "Without Gloves" -> {
                            ppePreferences["gloves"] = false
                            ppePreferences["no_gloves"] = true
                        }
                    }
                    when(radioBoots.text) {
                        "Both Boots" -> {
                            ppePreferences["boots"] = true
                            ppePreferences["no_boots"] = true
                        }
                        "No Boots" -> {
                            ppePreferences["boots"] = false
                            ppePreferences["no_boots"] = false
                        }
                        "With Boots" -> {
                            ppePreferences["boots"] = true
                            ppePreferences["no_boots"] = false
                        }
                        "Without Boots" -> {
                            ppePreferences["boots"] = false
                            ppePreferences["no_boots"] = true
                        }
                    }
                    val newPreferences = "{\"ppe_preferences\":" + JSONObject(Gson().toJson(ppePreferences)).toString() + "}"
                    Log.i("NewPreferences", newPreferences)

                    //setup mqtt publish
                    try {
                        if (mqttClient.isConnected()){
                            mqttClient.publish(setTopic,
                                newPreferences,
                                1,
                                false,
                                object : IMqttActionListener {
                                    override fun onSuccess(asyncActionToken: IMqttToken?) {
                                        val msg =
                                            "Publish message: $newPreferences to topic: $setTopic"
                                        Log.d(this.javaClass.name, msg)
                                        Toast.makeText(context,"Success", Toast.LENGTH_LONG).show()
                                    }

                                    override fun onFailure(
                                        asyncActionToken: IMqttToken?,
                                        exception: Throwable?
                                    ) {
                                        Log.d(
                                            this.javaClass.name,
                                            "Failed to publish message to topic"
                                        )
                                        Toast.makeText(context,"Failed", Toast.LENGTH_LONG).show()
                                    }
                                })
                        } else {
                            Log.d(this.javaClass.name, "Impossible to publish, no server connected")
                        }

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
                false -> {
                    Log.i("Helmet", "No radio button active")
                }
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}