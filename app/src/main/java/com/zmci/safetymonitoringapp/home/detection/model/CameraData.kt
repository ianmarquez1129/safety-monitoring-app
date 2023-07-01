package com.zmci.safetymonitoringapp.home.detection.model

data class CameraData(
    var id: Int = 0,
    var cameraName: String = "",
    var MQTT_TOPIC: String = "",
    var MQTT_CLIENT_ID: String = ""
)