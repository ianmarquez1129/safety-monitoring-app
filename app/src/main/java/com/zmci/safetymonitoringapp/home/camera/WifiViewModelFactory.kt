package com.zmci.safetymonitoringapp.home.camera

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.zmci.safetymonitoringapp.home.camera.repository.Repository

class WifiViewModelFactory(private val repository: Repository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return WifiViewModel(repository) as T
    }
}