package com.example.gki.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.gki.data.remote.RetrofitClient
import com.example.gki.data.repository.UserRepository
import com.example.gki.data.model.UserResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.io.InputStream

class CustomerViewModel : ViewModel() {
    private val repository = UserRepository(RetrofitClient.instance)

    private val _customers = MutableStateFlow<List<UserResponse>>(emptyList())
    val customers: StateFlow<List<UserResponse>> = _customers

    private val _currentUser = MutableStateFlow<UserResponse?>(null)
    val currentUser: StateFlow<UserResponse?> = _currentUser

    fun fetchAllUsers() {
        viewModelScope.launch {
            try {
                // Chạy song song cả 4 yêu cầu mạng
                val deferredUsers = (1..4).map { id ->
                    async { try { repository.getUserProfile(id) } catch (e: Exception) { null } }
                }
                val results = deferredUsers.awaitAll().filterNotNull()
                _customers.value = results
            } catch (e: Exception) {
                Log.e("DEBUG_API", "Lỗi tải danh sách: ${e.message}")
            }
        }
    }

    fun fetchCurrentUser(userId: Int) {
        viewModelScope.launch {
            try {
                _currentUser.value = repository.getUserProfile(userId)
            } catch (e: Exception) {
                Log.e("DEBUG_API", "Fetch User Error: ${e.message}")
            }
        }
    }

    fun updateProfile(userId: Int, fullName: String) {
        viewModelScope.launch {
            try {
                val updatedUser = repository.updateProfile(userId, fullName)
                _currentUser.value = updatedUser
                fetchAllUsers()
            } catch (e: Exception) {
                Log.e("DEBUG_API", "Update Profile Error: ${e.message}")
            }
        }
    }

    // --- HÀM MỚI: CẬP NHẬT ẢNH ĐẠI DIỆN ---
    fun updateProfileImage(context: Context, userId: Int, imageUri: Uri) {
        viewModelScope.launch {
            try {
                val base64Image = uriToBase64(context, imageUri)
                if (base64Image != null) {
                    Log.d("DEBUG_API", "Đang upload ảnh cho ID: $userId")
                    val updatedUser = repository.updateProfileImage(userId, "data:image/jpeg;base64,$base64Image")
                    _currentUser.value = updatedUser
                    fetchAllUsers()
                    Log.d("DEBUG_API", "Upload ảnh thành công!")
                }
            } catch (e: Exception) {
                Log.e("DEBUG_API", "Upload Image Error: ${e.message}")
            }
        }
    }

    fun updateUserHobbies(userId: Int, hobbies: String) {
        viewModelScope.launch {
            try {
                val updatedUser = repository.updateHobbies(userId, hobbies)
                _currentUser.value = updatedUser
                fetchAllUsers()
            } catch (e: Exception) {
                Log.e("DEBUG_API", "Update Hobbies Error: ${e.message}")
            }
        }
    }

    // Hỗ trợ chuyển đổi Uri sang Base64
    private fun uriToBase64(context: Context, uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) Base64.encodeToString(bytes, Base64.NO_WRAP) else null
        } catch (e: Exception) {
            null
        }
    }
}