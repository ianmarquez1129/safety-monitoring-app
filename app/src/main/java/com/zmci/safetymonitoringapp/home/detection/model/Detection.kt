package com.zmci.safetymonitoringapp.home.detection.model

data class Detection(
    var id: Int = 0,
    var image: String = "",
    var cameraName: String = "",
    var timestamp:String = "",
    var violators:String = "",
    var total_violations:String = "",
    var total_violators:String = "",
)