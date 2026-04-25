package com.example.gki.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.gki.Screen

@Composable
fun AppBottomNavigation(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(containerColor = Color.White) {
        NavigationBarItem(
            selected = currentScreen is Screen.Home,
            onClick = { onNavigate(Screen.Home) },
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text("Home") }
        )
        NavigationBarItem(
            selected = currentScreen is Screen.Chat,
            onClick = { onNavigate(Screen.Chat) },
            icon = { Icon(Icons.Default.Send, null) },
            label = { Text("Chat") }
        )
        NavigationBarItem(
            selected = currentScreen is Screen.Match,
            onClick = { onNavigate(Screen.Match) },
            icon = { Icon(Icons.Default.FavoriteBorder, null) },
            label = { Text("Match") }
        )
        NavigationBarItem(
            selected = currentScreen is Screen.HoSo,
            onClick = { onNavigate(Screen.HoSo) },
            icon = { Icon(Icons.Default.Person, null) },
            label = { Text("Hồ sơ") }
        )
    }
}