package com.example.gki.data.repository

import com.example.gki.data.model.UserResponse
import com.example.gki.data.remote.ApiService

class UserRepository(private val apiService: ApiService) {

    suspend fun getUserProfile(userId: Int): UserResponse {
        return apiService.getUserProfile(userId)
    }

    suspend fun updateHobbies(userId: Int, hobbies: String): UserResponse {
        return apiService.updateHobbies(userId, hobbies)
    }

    suspend fun updateProfile(userId: Int, fullName: String): UserResponse {
        return apiService.updateProfile(userId, fullName)
    }

    suspend fun updateProfileImage(userId: Int, imageBase64: String): UserResponse {
        return apiService.updateProfileImage(userId, imageBase64)
    }
}