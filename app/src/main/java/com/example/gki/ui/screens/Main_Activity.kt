package com.example.gki.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gki.Screen
import com.example.gki.viewmodel.CustomerViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreenContent(
    viewModel: CustomerViewModel,
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    val userList by viewModel.customers.collectAsState()
    val pagerState = rememberPagerState(pageCount = { userList.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dating & Chatting", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { viewModel.fetchAllUsers() }) { // Nút làm mới
                        Icon(Icons.Default.Refresh, "Reload", tint = Color(0xFFFD297B))
                    }
                }
            )
        },
        bottomBar = {
            AppBottomNavigation(currentScreen, onNavigate)
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF5F5F5))
        ) {
            if (userList.isEmpty()) {
                // Hiển thị thông báo thay vì chỉ xoay
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = Color(0xFFFD297B))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Đang tải dữ liệu hoặc lỗi kết nối...", color = Color.Gray)
                    Button(onClick = { viewModel.fetchAllUsers() }) {
                        Text("Thử lại")
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(20.dp))

                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(horizontal = 32.dp),
                        pageSpacing = 16.dp
                    ) { pageIndex ->
                        val user = userList[pageIndex]

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .graphicsLayer {
                                    val pageOffset = (pagerState.currentPage - pageIndex) + pagerState.currentPageOffsetFraction
                                    alpha = 1f - (pageOffset * 0.4f).coerceIn(0f, 1f)
                                },
                            shape = RoundedCornerShape(24.dp),
                            elevation = CardDefaults.cardElevation(8.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White)
                        ) {
                            Column {
                                AsyncImage(
                                    model = user.img_url,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxWidth().height(400.dp),
                                    contentScale = ContentScale.Crop
                                )

                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text(
                                        text = "${user.full_name ?: "Unknown"}, ${calculateAge(user.birth_date ?: "")}",
                                        color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Bold
                                    )
                                    Text("📏 ${user.height ?: "--"}  |  ⚖️ ${user.weight ?: "--"}", color = Color.Gray, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("Sở thích:", color = Color(0xFFFD297B), fontWeight = FontWeight.Bold)
                                    Text(user.hobbies ?: "Chưa cập nhật", color = Color.DarkGray, fontSize = 15.sp)
                                }
                            }
                        }
                    }

                    // --- NÚT LIKE / DISLIKE ---
                    Row(
                        modifier = Modifier.padding(vertical = 32.dp),
                        horizontalArrangement = Arrangement.spacedBy(40.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        LargeFloatingActionButton(
                            onClick = {
                                scope.launch { 
                                    if (pagerState.currentPage < userList.size - 1) {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                            },
                            containerColor = Color.White, contentColor = Color.Red, shape = CircleShape
                        ) {
                            Icon(Icons.Default.Close, "Dislike", modifier = Modifier.size(36.dp))
                        }

                        LargeFloatingActionButton(
                            onClick = {
                                scope.launch { 
                                    if (pagerState.currentPage < userList.size - 1) {
                                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                                    }
                                }
                            },
                            containerColor = Color.White, contentColor = Color(0xFF00E676), shape = CircleShape
                        ) {
                            Icon(Icons.Default.Favorite, "Like", modifier = Modifier.size(36.dp))
                        }
                    }
                }
            }
        }
    }
}

fun calculateAge(birthDateString: String): Int {
    if (birthDateString.isEmpty()) return 0
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val birthDate = LocalDate.parse(birthDateString, formatter)
        Period.between(birthDate, LocalDate.now()).years
    } catch (e: Exception) {
        0
    }
}
