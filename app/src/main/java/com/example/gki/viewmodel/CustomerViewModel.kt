package com.example.gki.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.gki.data.remote.RetrofitClient
import com.example.gki.data.repository.UserRepository
import com.example.gki.data.model.UserResponse
import androidx.compose.runtime.collectAsState

class CustomerViewModel : ViewModel() {
    private val repository = UserRepository(RetrofitClient.instance)

    // Quản lý danh sách người dùng
    private val _customers = MutableStateFlow<List<UserResponse>>(emptyList())
    val customers: StateFlow<List<UserResponse>> = _customers

    init {
        // Gọi hàm lấy danh sách khi khởi tạo
        fetchAllUsers()
    }

    fun fetchAllUsers() {
        viewModelScope.launch {
            try {
                // Trong thực tế, bạn nên viết thêm hàm getAllUsers trong ApiService
                // Ở đây mình giả lập lấy 4 người từ DB bạn vừa tạo
                val list = mutableListOf<UserResponse>()
                for (id in 1..4) {
                    list.add(repository.getUserProfile(id))
                }
                _customers.value = list
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}