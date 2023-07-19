package com.zmci.safetymonitoringapp.home

import java.util.Timer
import java.util.TimerTask

class RealtimeUpdate (
    private var pollingInterval: Long,
    private val callback_fun: ()->Unit,
    ){

    private var timer : Timer? = null

    fun startPolling(){
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask(){
            override fun run() {
                callback_fun()
            }
        },0,pollingInterval)
    }

    fun stopPolling(){
        timer?.cancel()
        timer = null
    }

}