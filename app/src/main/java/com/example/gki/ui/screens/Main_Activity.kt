package com.example.gki.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gki.Screen
import com.example.gki.viewmodel.CustomerViewModel
import com.example.gki.R
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreenContent(
    viewModel: CustomerViewModel,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    val userList by viewModel.customers.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    // Lọc bản thân: Không hiện chính mình trong danh sách quẹt
    val displayList = remember(userList, currentUser) {
        userList.filter { it.id_user != currentUser?.id_user }
    }

    val pagerState = rememberPagerState(pageCount = { displayList.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dating & Chatting", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.fetchAllUsers() }) {
                        Icon(Icons.Default.Refresh, "Reload", tint = Color(0xFFFD297B))
                    }
                }
            )
        },
        bottomBar = {
            AppBottomNavigation(currentScreen, onNavigate)
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding).background(Color(0xFFF5F5F5))) {
            if (displayList.isEmpty()) {
                Column(modifier = Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = Color(0xFFFD297B))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Đang tìm kiếm đối tượng...", color = Color.Gray)
                }
            } else {
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Spacer(modifier = Modifier.height(20.dp))

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 32.dp),
                        pageSpacing = 16.dp
                    ) { pageIndex ->
                        val user = displayList[pageIndex]
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                // --- THÊM CLICKABLE TẠI ĐÂY ---
                                .clickable {
                                    // Chuyển sang màn hình hồ sơ của người dùng này
                                    // Lưu ý: Bạn cần xử lý logic hiển thị dữ liệu của 'user' này trong HosoScreen
                                    onNavigate(Screen.HoSo)
                                }
                                .graphicsLayer {
                                    val pageOffset = (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
                                    alpha = 1f - (pageOffset * 0.4f).coerceIn(0f, 1f)
                                },
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column {
                                // --- ẢNH HIỆN Ô VUÔNG LỚN PHÍA TRÊN ---
                                AsyncImage(
                                    model = if (user.profile_img_id.isNullOrEmpty() || user.profile_img_id == "1")
                                        R.drawable.dai_dien else user.profile_img_id,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f) // Tạo hình vuông
                                        .background(Color.LightGray),
                                    contentScale = ContentScale.Crop,
                                    placeholder = painterResource(R.drawable.dai_dien),
                                    error = painterResource(R.drawable.dai_dien)
                                )

                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        text = "${user.full_name ?: "Unknown"}, ${calculateAge(user.birth_date ?: "")}",
                                        fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black
                                    )
                                    Text("📏 ${user.height ?: "--"}  |  ⚖️ ${user.weight ?: "--"}", color = Color.Gray)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("Sở thích:", color = Color(0xFFFD297B), fontWeight = FontWeight.Bold)

                                    Text(
                                        text = if (user.hobbies.isNullOrEmpty()) "Chưa cập nhật" else user.hobbies!!,
                                        color = Color.DarkGray, fontSize = 15.sp
                                    )
                                }
                            }
                        }
                    }

                    Row(modifier = Modifier.padding(vertical = 24.dp), Arrangement.spacedBy(40.dp), Alignment.CenterVertically) {
                        LargeFloatingActionButton(
                            onClick = {
                                scope.launch {
                                    if (pagerState.currentPage < displayList.size - 1) pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                            containerColor = Color.White, contentColor = Color.Red, shape = CircleShape
                        ) {
                            Icon(Icons.Default.Close, null, modifier = Modifier.size(32.dp))
                        }
                        LargeFloatingActionButton(
                            onClick = {
                                scope.launch {
                                    if (pagerState.currentPage < displayList.size - 1) pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                }
                            },
                            containerColor = Color.White, contentColor = Color(0xFF00E676), shape = CircleShape
                        ) {
                            Icon(Icons.Default.Favorite, null, modifier = Modifier.size(32.dp))
                        }
                    }
                }
            }
        }
    }
}

// Hàm tính tuổi tương thích API 24
fun calculateAge(birthDateString: String?): Int {
    if (birthDateString.isNullOrEmpty()) return 0
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val birthDate = sdf.parse(birthDateString) ?: return 0
        val birthCalendar = Calendar.getInstance().apply { time = birthDate }
        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) age--
        age
    } catch (e: Exception) { 0 }
}