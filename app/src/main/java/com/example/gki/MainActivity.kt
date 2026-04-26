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
import com.example.gki.viewmodel.CustomerViewModel
import com.example.gki.ui.screens.LoginScreen
import com.example.gki.ui.screens.MainScreenContent
import com.example.gki.ui.screens.HosoScreen
import com.example.gki.ui.screens.MatchScreen
import com.example.gki.ui.screens.TP_Hoso.EditAvataScreen
import com.example.gki.ui.screens.TP_Hoso.EditHobbiesScreen
import com.example.gki.ui.screens.TP_Hoso.UpImgScreen

sealed class Screen {
    object Login : Screen()
    object Home : Screen()
    object Chat : Screen()
    object Match : Screen()
    object HoSo : Screen()
    object EditAvata : Screen()
    object EditHobbies : Screen()
    object Up_Img : Screen()
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
                        onLoginSuccess = { userId ->
                            viewModel.fetchCurrentUser(userId)
                            viewModel.fetchAllUsers()
                            currentScreen = Screen.Home
                        },
                        onNavigateToSignUp = { }
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
                        onNavigate = { newScreen -> currentScreen = newScreen }
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
                    BackHandler { currentScreen = Screen.Home }
                    // Màn hình Chat của bạn
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
            }
        }
    }
}