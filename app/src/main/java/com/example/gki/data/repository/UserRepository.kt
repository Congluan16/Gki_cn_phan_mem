package com.example.gki.data.repository

import com.example.gki.data.model.UserResponse
import com.example.gki.data.remote.ApiService

class UserRepository(private val apiService: ApiService) {

    suspend fun getUserProfile(userId: Int): UserResponse {
        return apiService.getUserProfile(userId)
    }
}