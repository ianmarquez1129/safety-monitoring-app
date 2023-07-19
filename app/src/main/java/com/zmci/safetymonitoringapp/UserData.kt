package com.zmci.safetymonitoringapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.zmci.safetymonitoringapp.home.detection.model.Detection

// a singleton to hold user data (this is a ViewModel pattern, without inheriting from ViewModel)
object UserData {

    // signed in status
    private val _isSignedIn = MutableLiveData<Boolean>(false)
    var isSignedIn: LiveData<Boolean> = _isSignedIn

    private val _userEmail = MutableLiveData<String>("")
    var userEmail: LiveData<String> = _userEmail

    private val _userName = MutableLiveData<String>("")
    var userName : LiveData<String> = _userName

    private val _chart = MutableLiveData<Array<Int>>()
    var chart : LiveData<Array<Int>> = _chart

    private val _logs = MutableLiveData<ArrayList<Detection>>()
    var logs : LiveData<ArrayList<Detection>> = _logs

    private val _deviceLogs = MutableLiveData<ArrayList<Detection>>()
    var deviceLogs : LiveData<ArrayList<Detection>> = _deviceLogs

    private val _deviceStatus = MutableLiveData<ArrayList<HashMap<String,String>>>()
    var deviceStatus : LiveData<ArrayList<HashMap<String,String>>> = _deviceStatus

    fun setSignedIn(newValue : Boolean) {
        // use postvalue() to make the assignation on the main (UI) thread
        _isSignedIn.postValue(newValue)
    }
    fun setUserEmail(newValue : String) {
        _userEmail.postValue(newValue)
    }

    fun setUserName(newValue: String) {
        _userName.postValue(newValue)
    }

    fun setChart(newValue: Array<Int>) {
        _chart.postValue(newValue)
    }

    fun setLogs(newValue: ArrayList<Detection>) {
        _logs.postValue(newValue)
    }

    fun setDeviceLogs(newValue: ArrayList<Detection>) {
        _deviceLogs.postValue(newValue)
    }

    fun setDeviceStatus(newValue: ArrayList<HashMap<String,String>>) {
        _deviceStatus.postValue(newValue)
    }



}