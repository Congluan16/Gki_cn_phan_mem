package com.example.gki.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gki.data.model.MatchResponse
import com.example.gki.data.model.PostImageResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.gki.data.remote.RetrofitClient
import com.example.gki.data.repository.UserRepository
import com.example.gki.data.model.UserResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CustomerViewModel : ViewModel() {
    private val repository = UserRepository(RetrofitClient.instance)

    private val _customers = MutableStateFlow<List<UserResponse>>(emptyList())
    val customers: StateFlow<List<UserResponse>> = _customers

    private val _currentUser = MutableStateFlow<UserResponse?>(null)
    val currentUser: StateFlow<UserResponse?> = _currentUser

    fun fetchAllUsers() {
        viewModelScope.launch {
            try {
                // SỬA: Lấy toàn bộ danh sách từ API thay vì chạy vòng lặp id 1..4
                val results = apiService.getAllUsers()
                _customers.value = results
            } catch (e: Exception) {
                Log.e("DEBUG_API", "Lỗi tải danh sách: ${e.message}")
            }
        }
    }
    // Trong CustomerViewModel.kt
    // Trong CustomerViewModel.kt
    fun handleMatchAction(action: String, myId: Int, targetId: Int, context: android.content.Context) {
        if (myId == 0) return

        viewModelScope.launch {
            try {
                val response = apiService.manageMatch(action, myId, targetId)

                // Tải lại danh sách matches mới nhất từ Server
                fetchMatches(myId)

                // Hiện thông báo để người dùng biết nút đã được nhấn thành công
                val msg = if (action == "send") "Đã gửi lời mời kết bạn!" else "Đã trở thành bạn bè!"
                android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()

            } catch (e: Exception) {
                Log.e("DEBUG_API", "Lỗi thao tác match: ${e.message}")
            }
        }
    }
    fun login(email: String, pass: String, onSuccess: (Int) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Gọi hàm login từ Repository
                val user = repository.login(email, pass)
                if (user != null && user.id_user != 0) {
                    _currentUser.value = user // Lưu thông tin user hiện tại
                    onSuccess(user.id_user)
                } else {
                    onError("Email hoặc mật khẩu không chính xác")
                }
            } catch (e: Exception) {
                Log.e("DEBUG_API", "Login Error: ${e.message}")
                onError("Lỗi kết nối server")
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
    private suspend fun uriToBase64(context: Context, uri: Uri): String? = withContext(Dispatchers.IO) {
        return@withContext try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val bytes = inputStream?.readBytes()
            inputStream?.close()
            if (bytes != null) Base64.encodeToString(bytes, Base64.NO_WRAP) else null
        } catch (e: Exception) {
            Log.e("DEBUG_API", "Lỗi chuyển đổi ảnh: ${e.message}")
            null
        }
    }
    fun uploadPostImage(context: Context, userId: Int, imageUri: Uri, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                // 1. Kiểm tra việc chuyển đổi ảnh
                val base64Image = uriToBase64(context, imageUri)
                if (base64Image == null) {
                    Log.e("DEBUG_API", "LỖI: Không thể đọc được file ảnh từ Uri.")
                    return@launch
                }

                val fullBase64 = "data:image/jpeg;base64,$base64Image"
                Log.d("DEBUG_API", "Đang gửi yêu cầu upload cho User ID: $userId")

                // 2. Gọi API
                val response = RetrofitClient.instance.uploadPostImage(userId, fullBase64)

                // Nếu không có lỗi, Retrofit sẽ chạy tiếp xuống đây
                Log.d("DEBUG_API", "Server trả về: $response")
                onSuccess()

            } catch (e: retrofit2.HttpException) {
                // TRẠM KIỂM TRA 1: Lỗi từ phía Server (500, 404, 403...)
                val code = e.code()
                val errorBody = e.response()?.errorBody()?.string()

                // Chỗ này sẽ in ra nội dung như "Unknown column 'is_avatar'..." mà bạn thấy trong log Apache
                Log.e("DEBUG_API", "LỖI SERVER ($code): $errorBody")

            } catch (e: java.io.IOException) {
                // TRẠM KIỂM TRA 2: Lỗi kết nối (Mất mạng, Server sập, Timeout)
                Log.e("DEBUG_API", "LỖI KẾT NỐI: Kiểm tra lại Wi-Fi hoặc địa chỉ IP Server.")

            } catch (e: Exception) {
                // TRẠM KIỂM TRA 3: Các lỗi logic code khác
                Log.e("DEBUG_API", "LỖI NGOẠI LỆ: ${e.message}")
            }
        }
    }

    // Thêm biến lưu danh sách ảnh
    private val apiService = RetrofitClient.instance
    private val _userImages = MutableStateFlow<List<PostImageResponse>>(emptyList())
    val userImages: StateFlow<List<PostImageResponse>> = _userImages
    fun fetchUserImages(userId: Int) {
        viewModelScope.launch {
            try {
                // API giờ trả về List<PostImageResponse>
                val images = apiService.getPostImages(userId)
                _userImages.value = images
            } catch (e: Exception) {
                Log.e("DEBUG_API", "Lỗi tải ảnh post: ${e.message}")
            }
        }
    }
    fun deleteImage(imageId: Int, userId: Int) {
        viewModelScope.launch {
            try {
                apiService.deletePostImage(imageId)
                // Xóa xong thì tải lại danh sách ảnh mới
                fetchUserImages(userId)
            } catch (e: Exception) {
                Log.e("DEBUG_API", "Lỗi xóa ảnh: ${e.message}")
            }
        }
    }
    fun logout() {
        _currentUser.value = null
        _userImages.value = emptyList()
    }
    fun updateBasicInfo(userId: Int, birthDate: String, height: String, weight: String) {
        viewModelScope.launch {
            try {
                val updatedUser = apiService.updateBasicInfo(userId, birthDate, height, weight)
                _currentUser.value = updatedUser // Cập nhật lại UI ngay lập tức
            } catch (e: Exception) {
                Log.e("DEBUG_API", "Update Info Error: ${e.message}")
            }
        }
    }
    // Trong class CustomerViewModel
    private val _matches = MutableStateFlow<List<MatchResponse>>(emptyList())
    val matches: StateFlow<List<MatchResponse>> = _matches

    // 1. Tải danh sách các mối quan hệ từ server
    fun fetchMatches(myId: Int) {
        viewModelScope.launch {
            try {
                _matches.value = apiService.getMatches(myId)
            } catch (e: Exception) {
                Log.e("DEBUG_API", "Lỗi tải matches: ${e.message}")
            }
        }
    }

    // 2. Gửi hoặc Chấp nhận lời mời
    fun handleMatchAction(action: String, myId: Int, targetId: Int) {
        viewModelScope.launch {
            try {
                apiService.manageMatch(action, myId, targetId)
                // Sau khi thực hiện xong, tải lại danh sách để UI cập nhật ngay
                fetchMatches(myId)
            } catch (e: Exception) {
                Log.e("DEBUG_API", "Lỗi thao tác match: ${e.message}")
            }
        }
    }
}