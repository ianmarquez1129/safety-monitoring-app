package com.zmci.safetymonitoringapp

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

// a singleton to hold user data (this is a ViewModel pattern, without inheriting from ViewModel)
object UserData {

    // signed in status
    private val _isSignedIn = MutableLiveData<Boolean>(false)
    var isSignedIn: LiveData<Boolean> = _isSignedIn

    private val _userEmail = MutableLiveData<String>("")
    var userEmail: LiveData<String> = _userEmail

    private val _userName = MutableLiveData<String>("")
    var userName : LiveData<String> = _userName

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


}