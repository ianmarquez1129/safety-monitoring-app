package com.zmci.safetymonitoringapp.home.camera

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zmci.safetymonitoringapp.home.camera.model.Post
import com.zmci.safetymonitoringapp.home.camera.repository.Repository
import kotlinx.coroutines.launch
import retrofit2.Response


class ConnectCameraViewModel(private val repository: Repository) : ViewModel() {

    var myResponse: MutableLiveData<Response<Post>> = MutableLiveData()

    fun pushPost(post: Post) {
        viewModelScope.launch {
            val response = repository.pushPost(post)
            myResponse.value = response
        }
    }
    fun getPost(){
        viewModelScope.launch {
            val response = repository.getPost()
            myResponse.value = response
        }
    }

}