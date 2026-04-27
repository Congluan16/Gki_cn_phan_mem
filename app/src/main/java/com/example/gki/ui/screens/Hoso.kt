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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
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
import com.example.gki.data.model.PostImageResponse
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HosoScreen(
    viewModel: CustomerViewModel = viewModel(),
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    onLogout: () -> Unit // THÊM DÒNG NÀY
) {
    val user by viewModel.currentUser.collectAsState()
    val postImages by viewModel.userImages.collectAsState()

    LaunchedEffect(user?.id_user) {
        user?.id_user?.let { id -> viewModel.fetchUserImages(id) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Hồ sơ cá nhân",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {   // icon nằm bên phải
                    IconButton(onClick = onLogout) {
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Đăng xuất",
                            tint = Color(0xFFFD297B)
                        )
                    }
                }
            )
        },
        bottomBar = { AppBottomNavigation(currentScreen, onNavigate) }
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
                HosoHeader(
                    name = data.full_name ?: "Chưa có tên",
                    imageUrl = data.profile_img_id,
                    onImageClick = { onNavigate(Screen.EditAvata) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // TRUYỀN onClick VÀO ĐÂY
                HosoInfoSection(
                    birthDate = data.birth_date,
                    height = data.height,
                    weight = data.weight,
                    onClick = { onNavigate(Screen.EditInfo) } // Hết lỗi onClick
                )

                Spacer(modifier = Modifier.height(16.dp))

                HosoHobbiesSection(
                    hobbies = data.hobbies ?: "",
                    onClick = { onNavigate(Screen.EditHobbies) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(text = "Ảnh đã đăng", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(12.dp))

                HosoImageGrid(
                    images = postImages,
                    viewModel = viewModel,
                    onAddClick = { onNavigate(Screen.Up_Img) }
                )
            }
        }
    }
}

@Composable
fun HosoInfoSection(
    birthDate: String?,
    height: String?,
    weight: String?,
    onClick: () -> Unit // Tham số đã có sẵn
) {
    val age = calculateAgeLocal(birthDate)
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() } // THÊM DÒNG NÀY ĐỂ CARD NHẬN SỰ KIỆN CLICK
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, null, tint = Color(0xFFFD297B), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thông tin cơ bản", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                InfoItem(label = "Tuổi", value = if (age > 0) "$age" else "--")
                InfoItem(label = "Ngày sinh", value = birthDate ?: "--")
                InfoItem(label = "Cao", value = if (height != null) "${height}m" else "--")
                InfoItem(label = "Nặng", value = if (weight != null) "${weight}kg" else "--")
            }
        }
    }
}

@Composable
fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun HosoImageGrid(images: List<PostImageResponse>, viewModel: CustomerViewModel, onAddClick: () -> Unit) {
    var imageToDelete by remember { mutableStateOf<PostImageResponse?>(null) }
    val user by viewModel.currentUser.collectAsState()

    // Hộp thoại xác nhận xóa
    if (imageToDelete != null) {
        AlertDialog(
            onDismissRequest = { imageToDelete = null },
            title = { Text("Xóa ảnh") },
            text = { Text("Bạn có chắc chắn muốn xóa tấm ảnh này không?") },
            confirmButton = {
                TextButton(onClick = {
                    user?.id_user?.let { viewModel.deleteImage(imageToDelete!!.id_img, it) }
                    imageToDelete = null
                }) {
                    Text("Xóa", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { imageToDelete = null }) {
                    Text("Hủy")
                }
            }
        )
    }

    Box(modifier = Modifier.height(500.dp)) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Card(onClick = onAddClick, shape = RoundedCornerShape(12.dp)) {
                    Box(modifier = Modifier.fillMaxWidth().aspectRatio(3f/4f), contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Add, null, tint = Color(0xFFFD297B), modifier = Modifier.size(40.dp))
                    }
                }
            }
            items(images) { post ->
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(3f/4f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { imageToDelete = post } // Nhấn vào ảnh để chọn xóa
                ) {
                    AsyncImage(
                        model = post.img_url,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    // Dải hiển thị ngày giờ
                    Box(modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter).background(Color.Black.copy(0.5f)).padding(2.dp)) {
                        Text(post.created_at ?: "", color = Color.White, fontSize = 8.sp, modifier = Modifier.align(Alignment.Center))
                    }
                }
            }
        }
    }
}

@Composable
fun HosoHeader(name: String, imageUrl: String?, onImageClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)).clickable { onImageClick() }.background(Color.White).padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = if (imageUrl.isNullOrEmpty() || imageUrl == "1") R.drawable.dai_dien else imageUrl,
            contentDescription = null,
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
    Card(onClick = onClick, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Favorite, null, tint = Color(0xFFFD297B), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Sở thích", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = hobbies.ifEmpty { "Chưa cập nhật sở thích..." }, fontSize = 15.sp)
        }
    }
}

private fun calculateAgeLocal(birthDateString: String?): Int {
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