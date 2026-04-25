package com.example.gki.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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

val VKU_Pink = Color(0xFFFD297B)
val AppBackground = Color(0xFFF5F5F5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HosoScreen(
    viewModel: CustomerViewModel = viewModel(),
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    val user by viewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Hồ sơ cá nhân", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* Cài đặt */ }) {
                        Icon(Icons.Default.Settings, "Cài đặt", tint = VKU_Pink)
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
                .background(AppBackground)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            user?.let { data ->
                // CHỈNH SỬA TẠI ĐÂY: Dùng profile_img_id thay vì img_url
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

            } ?: run {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = VKU_Pink)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Ảnh đã đăng",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(12.dp))

            HosoImageGrid(
                imageIds = listOf(),
                onAddClick = { onNavigate(Screen.Up_Img) }
            )
        }
    }
}

@Composable
fun HosoHeader(
    name: String,
    imageUrl: String?,
    onImageClick: () -> Unit
) {
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
            // CHỈNH SỬA LOGIC: Nếu là "1" hoặc null thì hiện ảnh mặc định
            model = if (imageUrl.isNullOrEmpty() || imageUrl == "1") {
                R.drawable.dai_dien
            } else {
                imageUrl // Đây là URL ảnh từ server Ubuntu
            },
            contentDescription = "Avatar",
            placeholder = painterResource(R.drawable.dai_dien),
            error = painterResource(R.drawable.dai_dien),
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .border(2.dp, VKU_Pink, CircleShape),
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
fun HosoImageGrid(imageIds: List<Int>, onAddClick: () -> Unit) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxSize()
    ) {
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
                    Icon(Icons.Default.Add, "Đăng ảnh mới", tint = VKU_Pink, modifier = Modifier.size(40.dp))
                }
            }
        }
    }
}

@Composable
fun HosoHobbiesSection(hobbies: String, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Favorite, null, tint = VKU_Pink, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sở thích", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = if (hobbies.isNullOrEmpty()) "Chưa cập nhật sở thích..." else hobbies,
                fontSize = 15.sp, color = Color.DarkGray, lineHeight = 20.sp
            )
        }
    }
}
