package com.example.gki.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.gki.Screen
import com.example.gki.viewmodel.CustomerViewModel
import com.example.gki.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HosoScreen(
    viewModel: CustomerViewModel = viewModel(),
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    // 1. Lấy danh sách ảnh từ StateFlow trong ViewModel
    val postImages by viewModel.userImages.collectAsState()

    // 2. Tự động tải danh sách ảnh khi vào màn hình hoặc khi user thay đổi
    LaunchedEffect(user?.id_user) {
        user?.id_user?.let { id ->
            viewModel.fetchUserImages(id)
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Hồ sơ cá nhân", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* Cài đặt */ }) {
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
                .background(Color(0xFFF5F5F5))
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            user?.let { data ->
                HosoHeader(
                    name = data.full_name ?: "Chưa có tên",
                    imageUrl = data.profile_img_id,
                    onImageClick = { onNavigate(Screen.EditAvata) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                HosoHobbiesSection(
                    hobbies = data.hobbies ?: "",
                    onClick = { onNavigate(Screen.EditHobbies) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Ảnh đã đăng",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(12.dp))

                // 3. Truyền danh sách ảnh thực tế (postImages) vào Grid
                HosoImageGrid(
                    imageUrls = postImages,
                    onAddClick = { onNavigate(Screen.Up_Img) }
                )
            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFD297B))
                }
            }
        }
    }
}

@Composable
fun HosoImageGrid(imageUrls: List<String>, onAddClick: () -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // Ô đầu tiên luôn là nút "Thêm ảnh"
        item {
            Card(
                onClick = onAddClick,
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().aspectRatio(3f / 4f),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, "Đăng ảnh mới", tint = Color(0xFFFD297B), modifier = Modifier.size(40.dp))
                }
            }
        }

        // Vẽ các ảnh đã lấy được từ Server
        items(imageUrls) { url ->
            AsyncImage(
                model = url,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f / 4f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.dai_dien), // Ảnh hiện khi đang load
                error = painterResource(R.drawable.dai_dien)       // Ảnh hiện khi lỗi URL
            )
        }
    }
}

// Giữ nguyên HosoHeader và HosoHobbiesSection của bạn...
@Composable
fun HosoHeader(name: String, imageUrl: String?, onImageClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable { onImageClick() }
            .background(Color.White)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = if (imageUrl.isNullOrEmpty() || imageUrl == "1") R.drawable.dai_dien else imageUrl,
            contentDescription = "Avatar",
            modifier = Modifier.size(70.dp).clip(CircleShape).border(2.dp, Color(0xFFFD297B), CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = name, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(text = "Chỉnh sửa hồ sơ", fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun HosoHobbiesSection(hobbies: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Favorite, null, tint = Color(0xFFFD297B), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sở thích", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = if (hobbies.isEmpty()) "Chưa cập nhật sở thích..." else hobbies, fontSize = 15.sp, color = Color.DarkGray)
        }
    }
}