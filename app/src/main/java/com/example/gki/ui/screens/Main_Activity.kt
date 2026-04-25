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
    // Lấy danh sách người dùng từ ViewModel
    val userList by viewModel.customers.collectAsState()

    // Quản lý trạng thái lướt
    val pagerState = rememberPagerState(pageCount = { userList.size })
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Dating & Chatting", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.LocationOn, "Địa chỉ", tint = Color(0xFFFD297B))
                    }
                }
            )
        },
        bottomBar = {
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
    ) { padding ->
        if (userList.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFFD297B))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFFF5F5F5)),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // --- PHẦN LƯỚT CARD (Pager) ---
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
                                // Hiệu ứng mờ dần khi lướt
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
                                    text = "${user.full_name}, ${calculateAge(user.birth_date)}",
                                    color = Color.Black, fontSize = 24.sp, fontWeight = FontWeight.Bold
                                )
                                Text("📏 ${user.height}  |  ⚖️ ${user.weight}", color = Color.Gray, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Text("Sở thích:", color = Color(0xFFFD297B), fontWeight = FontWeight.Bold)
                                Text(user.hobbies, color = Color.DarkGray, fontSize = 15.sp)
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
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                        },
                        containerColor = Color.White, contentColor = Color.Red, shape = CircleShape
                    ) {
                        Icon(Icons.Default.Close, "Dislike", modifier = Modifier.size(36.dp))
                    }

                    LargeFloatingActionButton(
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
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

// Hàm hỗ trợ tính tuổi từ chuỗi "YYYY-MM-DD"
fun calculateAge(birthDateString: String): Int {
    return try {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val birthDate = LocalDate.parse(birthDateString, formatter)
        Period.between(birthDate, LocalDate.now()).years
    } catch (e: Exception) {
        0
    }
}

@Preview(showBackground = true, device = "id:pixel_7")
@Composable
fun PreviewMainScreen() {
    MaterialTheme {
        MainScreenContent(viewModel = viewModel(), currentScreen = Screen.Home, onNavigate = {})
    }
}