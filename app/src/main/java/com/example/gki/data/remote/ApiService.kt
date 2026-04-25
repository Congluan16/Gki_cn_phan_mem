package com.example.gki.data.remote

import com.example.gki.data.model.UserResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("get_user_profile.php")
    suspend fun getUserProfile(@Query("id") userId: Int): UserResponse
}