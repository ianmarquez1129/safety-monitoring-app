package com.zmci.safetymonitoringapp.home.camera.api

import com.zmci.safetymonitoringapp.home.camera.model.Post
import retrofit2.Response
import retrofit2.http.*
interface WifiApi {

    // for testing change '/' to /posts/1
    @GET("/posts/1")
    suspend fun getPost(): Response<Post>

    @POST("posts/1")
    suspend fun pushPost(
        @Body post: Post
    ): Response<Post>

}