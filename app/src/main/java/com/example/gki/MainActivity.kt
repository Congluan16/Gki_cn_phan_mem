package com.example.gki

import android.os.Bundle
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gki.ui.screens.ChatScreen
import com.example.gki.viewmodel.CustomerViewModel
import com.example.gki.ui.screens.LoginScreen
import com.example.gki.ui.screens.MainScreenContent
import com.example.gki.ui.screens.HosoScreen
import com.example.gki.ui.screens.Hoso_Khach
import com.example.gki.ui.screens.MatchScreen
import com.example.gki.ui.screens.SignUpScreen
import com.example.gki.ui.screens.TP_Hoso.EditAvataScreen
import com.example.gki.ui.screens.TP_Hoso.EditHobbiesScreen
import com.example.gki.ui.screens.TP_Hoso.EditInfoScreen
import com.example.gki.ui.screens.TP_Hoso.UpImgScreen

sealed class Screen {
    object SignUp : Screen()
    object Login : Screen()
    object Home : Screen()
    object Chat : Screen()
    object Match : Screen()
    object HoSo : Screen()

    data class HosoKhach(
        val userId: Int,
        val fromScreen: Screen
    ) : Screen()
    data class ChatBox(val userId: Int) : Screen()
    object EditAvata : Screen()
    object EditHobbies : Screen()
    object Up_Img : Screen()
    object EditInfo : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: CustomerViewModel = viewModel()
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

            when (currentScreen) {
                is Screen.Login -> {
                    LoginScreen(
                        viewModel = viewModel,
                        onLoginSuccess = { userId ->
                            viewModel.fetchMatches(userId)
                            // 1. Gọi hàm này để lấy hồ sơ đầy đủ (bao gồm sở thích) ngay lập tức
                            viewModel.fetchCurrentUser(userId)

                            // 2. Tải danh sách người dùng khác cho màn hình Home
                            viewModel.fetchAllUsers()

                            currentScreen = Screen.Home
                        },
                        onNavigateToSignUp = {
                            // Sửa dấu { } trống thành dòng dưới đây:
                            currentScreen = Screen.SignUp
                        }
                    )
                }
                is Screen.SignUp -> {
                    SignUpScreen(
                        viewModel = viewModel,
                        onSignUpSuccess = {
                            currentScreen = Screen.Login // Hoặc Screen.Home tùy bạn
                        },
                        onNavigateToLogin = {
                            currentScreen = Screen.Login // Lệnh này giúp nút hoạt động
                        }
                    )
                }
                is Screen.Home -> {
                    MainScreenContent(
                        viewModel = viewModel,
                        currentScreen = currentScreen,
                        onNavigate = { newScreen -> currentScreen = newScreen }
                    )
                }
                is Screen.HoSo -> {
                    HosoScreen(
                        viewModel = viewModel,
                        currentScreen = currentScreen,
                        onNavigate = { newScreen -> currentScreen = newScreen },
                        onLogout = {
                            viewModel.logout()
                            currentScreen = Screen.Login
                        }
                    )
                }
                is Screen.EditInfo -> {
                    val user by viewModel.currentUser.collectAsState()
                    EditInfoScreen(
                        currentUser = user,
                        onBack = { currentScreen = Screen.HoSo },
                        onSave = { birth, height, weight ->
                            user?.id_user?.let { id ->
                                viewModel.updateBasicInfo(id, birth, height, weight)
                            }
                            currentScreen = Screen.HoSo
                        }
                    )
                }
                is Screen.EditHobbies -> {
                    val user by viewModel.currentUser.collectAsState()
                    EditHobbiesScreen(
                        currentHobbies = user?.hobbies ?: "",
                        onBack = { currentScreen = Screen.HoSo },
                        onSave = { newHobbies ->
                            user?.id_user?.let { id ->
                                viewModel.updateUserHobbies(id, newHobbies)
                            }
                            currentScreen = Screen.HoSo
                        }
                    )
                }
                is Screen.EditAvata -> {
                    val user by viewModel.currentUser.collectAsState()
                    EditAvataScreen(
                        currentName = user?.full_name ?: "",
                        currentImageUrl = user?.profile_img_id,
                        onBack = { currentScreen = Screen.HoSo },
                        onSave = { newName, uri ->
                            user?.id_user?.let { id ->
                                viewModel.updateProfile(id, newName)
                                uri?.let { selectedUri ->
                                    viewModel.updateProfileImage(this@MainActivity, id, selectedUri)
                                }
                            }
                            currentScreen = Screen.HoSo
                        }
                    )
                }
                is Screen.Up_Img -> {
                    val user by viewModel.currentUser.collectAsState()
                    UpImgScreen(
                        onBack = { currentScreen = Screen.HoSo },
                        onUpload = { uri ->
                            user?.id_user?.let { id ->
                                viewModel.uploadPostImage(this@MainActivity, id, uri) {
                                    viewModel.fetchUserImages(id) // Tải lại danh sách ảnh ngay khi upload xong
                                    currentScreen = Screen.HoSo
                                }
                            }
                        }
                    )
                }
                is Screen.Chat -> {
                    // Xử lý nút quay lại: Khi nhấn back trên điện thoại sẽ về Home
                    BackHandler {
                        currentScreen = Screen.Home
                    }

                    ChatScreen(
                        viewModel = viewModel,
                        currentScreen = Screen.Chat,
                        onNavigate = { nextScreen ->
                            // Chỉ cần gán giá trị mới cho biến currentScreen là màn hình tự đổi
                            currentScreen = nextScreen
                        }
                    )
                }
                is Screen.Match -> {
                    // Xử lý nút quay lại của hệ thống để về màn hình Home
                    BackHandler { currentScreen = Screen.Home }

                    MatchScreen(
                        viewModel = viewModel,
                        currentScreen = currentScreen,
                        onNavigate = { newScreen -> currentScreen = newScreen }
                    )
                }
                is Screen.HosoKhach -> {
                    val screen = currentScreen as Screen.HosoKhach
                    val guest = viewModel.customers.collectAsState().value
                        .find { it.id_user == screen.userId }

                    guest?.let {
                        Hoso_Khach(
                            guest = it,
                            viewModel = viewModel,
                            currentScreen = currentScreen,   // thêm dòng này
                            fromScreen = screen.fromScreen,  // thêm dòng này
                            onNavigate = { newScreen ->
                                currentScreen = newScreen
                            }
                        )
                    }
                }
                is Screen.ChatBox -> {
                    val screen = currentScreen as Screen.ChatBox
                    val user = viewModel.customers.collectAsState().value
                        .find { it.id_user == screen.userId }

                    user?.let {
                        com.example.gki.ui.screens.TP_Chat.Khung_Chat(
                            user = it,
                            viewModel = viewModel, // Thêm dòng này
                            onBack = { currentScreen = Screen.Chat }
                        )
                    }
                }
            }
        }
    }
}