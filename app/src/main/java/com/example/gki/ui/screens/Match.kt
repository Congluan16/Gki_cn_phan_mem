package com.example.gki.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check // Icon dấu tích đồng ý
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send // Dùng thay cho Chat để tránh lỗi thư viện icon extended
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset // Quan trọng để thanh gạch chân chạy mượt
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gki.Screen
import com.example.gki.data.model.UserResponse
import com.example.gki.viewmodel.CustomerViewModel
import com.example.gki.R

// --- HÀM DÙNG CHUNG: HIỂN THỊ AVATAR ---
@Composable
fun UserAvatar(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 60.dp
) {
    AsyncImage(
        // Hiển thị ảnh mặc định nếu URL rỗng hoặc bằng "1"
        model = if (imageUrl.isNullOrEmpty() || imageUrl == "1") {
            R.drawable.dai_dien
        } else {
            imageUrl
        },
        contentDescription = "Avatar",
        modifier = modifier
            .size(size)
            .clip(CircleShape),
        contentScale = ContentScale.Crop,
        placeholder = painterResource(R.drawable.dai_dien),
        error = painterResource(R.drawable.dai_dien)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchScreen(
    viewModel: CustomerViewModel = viewModel(),
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    val userList by viewModel.customers.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Khám phá, 1: Bạn bè, 2: Lời mời
    val tabs = listOf("Khám phá", "Bạn bè", "Lời mời", "Xác nhận")

    // Tự động tải danh sách người dùng khi vào màn hình
    LaunchedEffect(Unit) {
        if (userList.isEmpty()) viewModel.fetchAllUsers()
    }

    // Lọc danh sách theo tên dựa trên thanh tìm kiếm
    val filteredList = userList.filter {
        it.full_name?.contains(searchQuery, ignoreCase = true) == true
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                CenterAlignedTopAppBar(
                    title = { Text("Kết nối", fontWeight = FontWeight.Bold) }
                )
                // --- THANH TAB ---
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = Color(0xFFFD297B), // Màu hồng VKU
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = Color(0xFFFD297B)
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) Color(0xFFFD297B) else Color.Gray
                                )
                            }
                        )
                    }
                }
            }
        },
        bottomBar = {
            AppBottomNavigation(currentScreen, onNavigate)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
                .padding(horizontal = 16.dp)
        ) {
            // Thanh tìm kiếm
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                placeholder = {
                    Text(
                        when(selectedTab) {
                            0 -> "Tìm người mới..."
                            1 -> "Tìm bạn bè..."
                            else -> "Tìm lời mời..."
                        }
                    )
                },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFD297B),
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            // Danh sách hiển thị thay đổi theo Tab
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                when (selectedTab) {
                    0 -> { // TAB KHÁM PHÁ
                        items(filteredList) { user ->
                            MatchUserItem(
                                user = user,
                                actionType = "add",
                                onActionClick = { /* Logic gửi lời mời kết bạn */ }
                            )
                        }
                    }
                    1 -> { // TAB BẠN BÈ (Giả lập lấy 2 người đầu)
                        val friendsList = filteredList.take(2)
                        items(friendsList) { friend ->
                            MatchUserItem(
                                user = friend,
                                actionType = "chat",
                                onActionClick = { onNavigate(Screen.Chat) } // Điều hướng tới Chat
                            )
                        }
                    }
                    2 -> { // TAB LỜI MỜI (Giả lập lấy những người còn lại)
                        val requestList = filteredList.drop(2).take(2)
                        items(requestList) { requester ->
                            MatchUserItem(
                                user = requester,
                                actionType = "request",
                                onActionClick = { /* Logic chấp nhận lời mời */ }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MatchUserItem(
    user: UserResponse,
    actionType: String, // "add", "chat", "request"
    onActionClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { /* Xem chi tiết */ },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sử dụng ảnh đại diện từ trường profile_img_id
            UserAvatar(imageUrl = user.profile_img_id, size = 60.dp)

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.full_name ?: "Unknown",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = when(actionType) {
                        "chat" -> "Đang hoạt động"
                        "request" -> "Muốn kết bạn với bạn"
                        else -> "Sở thích: ${user.hobbies ?: "..."}"
                    },
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }

            // Nút hành động thay đổi Icon theo Tab
            IconButton(
                onClick = onActionClick,
                modifier = Modifier.background(
                    if (actionType == "chat") Color.LightGray.copy(alpha = 0.2f)
                    else Color(0xFFFD297B).copy(alpha = 0.1f),
                    CircleShape
                )
            ) {
                Icon(
                    imageVector = when(actionType) {
                        "chat" -> Icons.Default.Send // Nút gửi tin nhắn
                        "request" -> Icons.Default.Check // Nút đồng ý lời mời (Dấu tích)
                        else -> Icons.Default.Add // Nút kết bạn
                    },
                    contentDescription = null,
                    tint = if (actionType == "chat") Color.Gray else Color(0xFFFD297B) // Màu hồng VKU
                )
            }
        }
    }
}