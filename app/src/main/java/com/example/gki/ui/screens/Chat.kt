//package com.example.gki.ui.screens
//
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Settings
//import androidx.compose.material3.CenterAlignedTopAppBar
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.text.font.FontWeight
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.gki.Screen
//import com.example.gki.viewmodel.CustomerViewModel
//
//@Composable
//fun ChatScreen(
//    viewModel: CustomerViewModel = viewModel(),
//    currentScreen: Screen,
//    onNavigate: (Screen) -> Unit
//) {
//    val user by viewModel.currentUser.collectAsState()
//    Scaffold(
//        topBar = {
//            CenterAlignedTopAppBar(
//                title = { Text("Hồ sơ cá nhân", fontWeight = FontWeight.Bold) },
//                actions = {
//                    IconButton(onClick = { }) {
//                        Icon(Icons.Default.Settings, "Cài đặt", tint = VKU_Pink)
//                    }
//                }
//            )
//        },
//        bottomBar = {
//            AppBottomNavigation(currentScreen, onNavigate)
//        }
//    ) { padding ->