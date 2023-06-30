package com.zmci.safetymonitoringapp.home.camera.repository

import com.zmci.safetymonitoringapp.home.camera.api.RetrofitInstance
import com.zmci.safetymonitoringapp.home.camera.model.Post
import retrofit2.Response

class Repository {

    suspend fun getPost(): Response<Post> {
        return RetrofitInstance.api.getPost()
    }

    suspend fun pushPost(post: Post): Response<Post> {
        return RetrofitInstance.api.pushPost(post)
    }

}