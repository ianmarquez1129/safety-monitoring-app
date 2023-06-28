package com.zmci.safetymonitoringapp

import android.app.Application

class SafetyMonitoringApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // initialize Amplify when application is starting
        Backend.initialize(applicationContext)
    }
}