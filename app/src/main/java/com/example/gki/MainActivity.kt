package com.example.gki
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.gki.viewmodel.CustomerViewModel
import androidx.compose.runtime.setValue
import com.example.gki.ui.screens.LoginScreen
import com.example.gki.ui.screens.MainScreenContent

sealed class Screen {
    object Login : Screen()
    object Home : Screen()
    object Chat : Screen()
    object Match : Screen()
    object HoSo : Screen()
}
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: CustomerViewModel = viewModel()
            var currentScreen by remember { mutableStateOf<Screen>(Screen.Login) }

            when (currentScreen) {
                is Screen.Login -> {
                    // 2. Gọi đúng LoginScreen ở đây
                    LoginScreen(
                        onLoginSuccess = {
                            currentScreen = Screen.Home // Chuyển sang Home khi thành công
                        },
                        onNavigateToSignUp = {

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
                is Screen.Chat -> {
                    BackHandler { currentScreen = Screen.Home }
                    // Màn hình Chat của bạn
                }
                is Screen.Match -> {
                    BackHandler { currentScreen = Screen.Home }
                    // Màn hình Match
                }
                is Screen.HoSo -> {
                    BackHandler { currentScreen = Screen.Home }
                    // Màn hình Hồ Sơ
                }
            }
        }
    }
}