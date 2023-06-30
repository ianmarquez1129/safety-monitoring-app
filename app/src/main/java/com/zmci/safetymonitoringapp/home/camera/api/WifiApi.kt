package com.zmci.safetymonitoringapp.home.camera.api

import com.zmci.safetymonitoringapp.home.camera.model.Post
import retrofit2.Response
import retrofit2.http.*
interface WifiApi {

    @GET("/")
    suspend fun getPost(): Response<Post>

    @POST("/")
    suspend fun pushPost(
        @Body post: Post
    ): Response<Post>

}