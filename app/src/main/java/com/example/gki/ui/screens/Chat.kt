package com.example.gki.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gki.R
import com.example.gki.Screen
import com.example.gki.viewmodel.CustomerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: CustomerViewModel = viewModel(),
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    val users by viewModel.customers.collectAsState()
    val matches by viewModel.matches.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    val friendIds = remember(matches, currentUser) {
        matches.filter { it.status == 1 } // 1 = bạn bè
            .flatMap { match ->
                listOf(match.user_one_id, match.user_two_id)
            }
            .filter { it != currentUser?.id_user }
            .toSet()
    }
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Đoạn chat", fontWeight = FontWeight.ExtraBold) },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Settings, "Cài đặt", tint = Color(0xFFFD297B))
                    }
                }
            )
        },
        bottomBar = {
            AppBottomNavigation(currentScreen, onNavigate)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color.White)
        ) {
            // 1. THANH TÌM KIẾM (Tìm kiếm bạn bè)
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Tìm kiếm bạn bè", fontSize = 14.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                // Sửa đoạn này:
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF5F5F5),   // Màu nền khi nhấn vào
                    unfocusedContainerColor = Color(0xFFF5F5F5), // Màu nền bình thường
                    focusedIndicatorColor = Color(0xFFFD297B),   // Màu viền khi nhấn vào
                    unfocusedIndicatorColor = Color.Transparent, // Màu viền bình thường
                    disabledContainerColor = Color(0xFFF5F5F5)
                ),
                singleLine = true
            )

            // 2. DANH SÁCH BẠN BÈ/GHI CHÚ (Ngang)
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Nút thêm (Dấu + trong bản vẽ)
                item {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFEEEEEE))
                                .clickable { /* Hành động thêm */ },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray)
                        }
                        Text("Ghi chú bạn", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(top = 4.dp))
                    }
                }

                // Danh sách avatar bạn bè (user1, user2...)
                items(users.filter { it.id_user in friendIds }) { friend ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AsyncImage(
                            model = if (friend.profile_img_id.isNullOrEmpty() || friend.profile_img_id == "1")
                                R.drawable.dai_dien else friend.profile_img_id,
                            contentDescription = null,
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .border(2.dp, Color(0xFFFD297B), CircleShape),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = friend.full_name?.split(" ")?.last() ?: "User",
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 3. DANH SÁCH TIN NHẮN (Dọc - Tên và Nội dung)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(users.filter { it.id_user in friendIds }) { chatUser ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onNavigate(Screen.ChatBox(chatUser.id_user))
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar
                        AsyncImage(
                            model = if (chatUser.profile_img_id.isNullOrEmpty() || chatUser.profile_img_id == "1")
                                R.drawable.dai_dien else chatUser.profile_img_id,
                            contentDescription = null,
                            modifier = Modifier
                                .size(55.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        // Tên và Nội dung tin nhắn
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = chatUser.full_name ?: "Người dùng",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Nội dung tin nhắn mới nhất hiển thị tại đây...", // Phần "nội dung" trong bản vẽ
                                fontSize = 14.sp,
                                color = Color.Gray,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}