package com.zmci.safetymonitoringapp

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

// a singleton to hold user data (this is a ViewModel pattern, without inheriting from ViewModel)
object UserData {

    // signed in status
    private val _isSignedIn = MutableLiveData<Boolean>(false)
    var isSignedIn: LiveData<Boolean> = _isSignedIn

    fun setSignedIn(newValue : Boolean) {
        // use postvalue() to make the assignation on the main (UI) thread
        _isSignedIn.postValue(newValue)
    }


}