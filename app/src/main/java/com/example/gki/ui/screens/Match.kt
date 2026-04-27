package com.example.gki.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // QUAN TRỌNG: Phải có dòng này
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gki.Screen
import com.example.gki.data.model.MatchResponse
import com.example.gki.data.model.UserResponse
import com.example.gki.viewmodel.CustomerViewModel
import com.example.gki.R

@Composable
fun UserAvatar(
    imageUrl: String?,
    modifier: Modifier = Modifier,
    size: Dp = 60.dp
) {
    AsyncImage(
        model = if (imageUrl.isNullOrEmpty() || imageUrl == "1") R.drawable.dai_dien else imageUrl,
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
    val allUsers by viewModel.customers.collectAsState()
    val matches by viewModel.matches.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val myId = currentUser?.id_user ?: 0

    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Khám phá", "Bạn bè", "Lời mời", "Chờ xác nhận")

    LaunchedEffect(myId) {
        Log.d("DEBUG_API", "ID hiện tại của tôi là: $myId")
        if (myId != 0) {
            viewModel.fetchAllUsers()
            viewModel.fetchMatches(myId)
        }
    }

    // --- LOGIC LỌC DỮ LIỆU ---
    val invitesReceived = allUsers.filter { user ->
        matches.any { match: MatchResponse ->
            match.status == 0 && match.sender_id != myId && (match.user_one_id == user.id_user || match.user_two_id == user.id_user)
        }
    }

    val invitesSent = allUsers.filter { user ->
        matches.any { match: MatchResponse ->
            match.status == 0 && match.sender_id == myId && (match.user_one_id == user.id_user || match.user_two_id == user.id_user)
        }
    }

    val friends = allUsers.filter { user ->
        matches.any { match: MatchResponse ->
            match.status == 1 && (match.user_one_id == user.id_user || match.user_two_id == user.id_user)
        }
    }

    val discovery = allUsers.filter { user ->
        user.id_user != myId && matches.none { match: MatchResponse ->
            match.user_one_id == user.id_user || match.user_two_id == user.id_user
        }
    }

    Scaffold(
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                CenterAlignedTopAppBar(title = { Text("Kết nối", fontWeight = FontWeight.Bold) })
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    placeholder = { Text("Tìm kiếm...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    shape = RoundedCornerShape(12.dp)
                )
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.White,
                    contentColor = Color(0xFFFD297B),
                    edgePadding = 16.dp,
                    divider = {}
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = { Text(title, fontSize = 14.sp) }
                        )
                    }
                }
            }
        },
        bottomBar = { AppBottomNavigation(currentScreen, onNavigate) }
    ) { padding ->
        // --- ĐÂY LÀ PHẦN HIỂN THỊ DANH SÁCH ---
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            when (selectedTab) {
                0 -> { // KHÁM PHÁ
                    items(discovery.filter { it.full_name?.contains(searchQuery, true) == true }) { user ->
                        MatchUserItem(user, "add", onActionClick = {
                            viewModel.handleMatchAction("send", myId, user.id_user, context)
                        })
                    }
                }
                1 -> { // BẠN BÈ
                    items(friends) { friend ->
                        MatchUserItem(friend, "chat", onActionClick = {
                            onNavigate(Screen.Chat)
                        })
                    }
                }
                2 -> { // LỜI MỜI (Phải có X và Tích)
                    items(invitesReceived) { requester ->
                        MatchUserItem(
                            user = requester,
                            actionType = "request",
                            onActionClick = {
                                viewModel.handleMatchAction("accept", myId, requester.id_user, context)
                            },
                            onSecondaryActionClick = {
                                viewModel.handleMatchAction("decline", myId, requester.id_user, context)
                            }
                        )
                    }
                }
                3 -> { // CHỜ XÁC NHẬN (Cũng có thể Hủy bằng nút X)
                    items(invitesSent) { user ->
                        MatchUserItem(
                            user = user,
                            actionType = "pending",
                            onActionClick = { /* Đang chờ, không làm gì */ },
                            onSecondaryActionClick = {
                                viewModel.handleMatchAction("decline", myId, user.id_user, context)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MatchUserItem(
    user: UserResponse,
    actionType: String,
    onActionClick: () -> Unit,
    onSecondaryActionClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UserAvatar(imageUrl = user.profile_img_id)

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = user.full_name ?: "Unknown", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(
                    text = when(actionType) {
                        "chat" -> "Đang hoạt động"
                        "request" -> "Muốn kết bạn với bạn"
                        "pending" -> "Đang chờ xác nhận..."
                        else -> "Sở thích: ${user.hobbies ?: "Chưa có"}"
                    },
                    fontSize = 13.sp, color = Color.Gray
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // NÚT PHỤ (Dấu X - Chỉ hiện cho Lời mời và Chờ xác nhận)
                if ((actionType == "request" || actionType == "pending") && onSecondaryActionClick != null) {
                    IconButton(
                        onClick = onSecondaryActionClick,
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(Color.LightGray.copy(0.2f), CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(Icons.Default.Clear, contentDescription = "Hủy/Từ chối", tint = Color.Gray, modifier = Modifier.size(20.dp))
                    }
                }

                // NÚT CHÍNH (+ hoặc Tích hoặc Gửi tin nhắn)
                IconButton(
                    onClick = onActionClick,
                    modifier = Modifier
                        .background(
                            if (actionType == "chat") Color.LightGray.copy(0.2f) else Color(0xFFFD297B).copy(0.1f),
                            CircleShape
                        )
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = when(actionType) {
                            "chat" -> Icons.Default.Send
                            "request" -> Icons.Default.Check
                            "pending" -> Icons.Default.Search
                            else -> Icons.Default.Add
                        },
                        contentDescription = null,
                        tint = if (actionType == "chat") Color.Gray else Color(0xFFFD297B)
                    )
                }
            }
        }
    }
}