package com.example.gki.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gki.data.model.UserResponse
import com.example.gki.data.remote.RetrofitClient
import com.example.gki.data.repository.UserRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val repository = UserRepository(RetrofitClient.instance)

    var userProfile by mutableStateOf<UserResponse?>(null)
    var isLoading by mutableStateOf(false)

    fun fetchUserData(userId: Int) {
        viewModelScope.launch {
            isLoading = true
            try {
                val response = repository.getUserProfile(userId)
                userProfile = response
            } catch (e: Exception) {
                e.printStackTrace()
                // Bạn có thể thêm biến error để báo lỗi lên màn hình
            } finally {
                isLoading = false
            }
        }
    }
}