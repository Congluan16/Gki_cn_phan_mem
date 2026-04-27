package com.example.gki.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HosoScreen(
    viewModel: CustomerViewModel = viewModel(),
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    val user by viewModel.currentUser.collectAsState()
    val postImages by viewModel.userImages.collectAsState()

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
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            user?.let { data ->
                // 1. Header (Tên và Ảnh)
                HosoHeader(
                    name = data.full_name ?: "Chưa có tên",
                    imageUrl = data.profile_img_id,
                    onImageClick = { onNavigate(Screen.EditAvata) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Ô THÔNG TIN CƠ BẢN (Tuổi, Cao, Nặng, Ngày sinh)
                HosoInfoSection(
                    birthDate = data.birth_date,
                    height = data.height,
                    weight = data.weight
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Sở thích
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
fun HosoInfoSection(birthDate: String?, height: String?, weight: String?) {
    val age = calculateAgeLocal(birthDate)
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null, tint = Color(0xFFFD297B), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thông tin cơ bản", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoColumn(label = "Tuổi", value = if (age > 0) "$age" else "--")
                InfoColumn(label = "Ngày sinh", value = birthDate ?: "--")
                InfoColumn(label = "Cao", value = if (height != null) "${height}cm" else "--")
                InfoColumn(label = "Nặng", value = if (weight != null) "${weight}kg" else "--")
            }
        }
    }
}

@Composable
fun InfoColumn(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}

// Các Component con giữ nguyên logic nhưng đảm bảo sạch lỗi
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

@Composable
fun HosoImageGrid(imageUrls: List<String>, onAddClick: () -> Unit) {
    Box(modifier = Modifier.height(400.dp)) {
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
                    Box(modifier = Modifier.fillMaxWidth().aspectRatio(3f/4f), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, null, tint = Color(0xFFFD297B), modifier = Modifier.size(40.dp))
                    }
                }
            }
            items(imageUrls) { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().aspectRatio(3f/4f).clip(RoundedCornerShape(12.dp)).background(Color.LightGray),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.dai_dien),
                    error = painterResource(R.drawable.dai_dien)
                )
            }
        }
    }
}

// Đổi tên thành calculateAgeLocal và thêm private để tránh xung đột với file khác
private fun calculateAgeLocal(birthDateString: String?): Int {
    if (birthDateString.isNullOrEmpty()) return 0
    return try {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val birthDate = sdf.parse(birthDateString) ?: return 0
        val birthCalendar = Calendar.getInstance().apply { time = birthDate }
        val today = Calendar.getInstance()

        var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)

        // So sánh ngày trong năm để tính tuổi chính xác
        val dayInYearToday = today.get(Calendar.DAY_OF_YEAR)
        val dayInYearBirth = birthCalendar.get(Calendar.DAY_OF_YEAR)

        if (dayInYearToday < dayInYearBirth) {
            age--
        }
        age
    } catch (e: Exception) { 0 }
}