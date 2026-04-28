package com.example.gki.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.gki.Screen
import com.example.gki.data.model.MatchResponse
import com.example.gki.data.model.PostImageResponse
import com.example.gki.data.model.UserResponse
import com.example.gki.viewmodel.CustomerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Hoso_Khach(
    guest: UserResponse,
    viewModel: CustomerViewModel,
    currentScreen: Screen,
    fromScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    val context = LocalContext.current
    val currentUser by viewModel.currentUser.collectAsState()
    val matches by viewModel.matches.collectAsState()
    val postImages by viewModel.userImages.collectAsState()
    val myId = currentUser?.id_user ?: 0

    // Tìm trạng thái quan hệ giữa mình và người này
    val relation = matches.find {
        (it.user_one_id == guest.id_user || it.user_two_id == guest.id_user)
    }

    LaunchedEffect(guest.id_user) {
        viewModel.fetchUserImages(guest.id_user)
        if (myId != 0) viewModel.fetchMatches(myId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hồ sơ của ${guest.full_name}") },
                navigationIcon = {
                    IconButton(onClick = {
                        onNavigate(fromScreen)
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
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

            // --- HEADER: AVATAR, TÊN VÀ NÚT KẾT BẠN ---
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                UserAvatar(imageUrl = guest.profile_img_id, size = 120.dp)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = guest.full_name ?: "Unknown", fontWeight = FontWeight.Bold, fontSize = 22.sp)

                Spacer(modifier = Modifier.height(8.dp))

                // --- NÚT BẤM DƯỚI TÊN (KẾT BẠN / HỦY KẾT BẠN) ---
                Button(
                    onClick = {
                        val action = when {
                            relation == null -> "send"
                            relation.status == 1 -> "decline"
                            relation.sender_id == myId -> "decline"
                            else -> "accept"
                        }
                        viewModel.handleMatchAction(action, myId, guest.id_user, context)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (relation?.status == 1) Color.LightGray else Color(0xFFFD297B)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    val btnText = when {
                        relation == null -> "Kết bạn"
                        relation.status == 1 -> "Hủy kết bạn"
                        relation.sender_id == myId -> "Hủy yêu cầu"
                        else -> "Đồng ý kết bạn"
                    }
                    val icon = if (relation?.status == 1) Icons.Default.Close else Icons.Default.Add
                    Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(btnText)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- CÁC PHẦN THÔNG TIN (CHỈ ĐỌC) ---
            HosoInfoSection(
                birthDate = guest.birth_date,
                height = guest.height,
                weight = guest.weight,
                onClick = {} // Không làm gì vì không cho sửa
            )

            Spacer(modifier = Modifier.height(16.dp))

            HosoHobbiesSection(
                hobbies = guest.hobbies ?: "",
                onClick = {} // Không làm gì
            )

            Spacer(modifier = Modifier.height(20.dp))
            Text("Hình ảnh", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))

            // --- LƯỚI ẢNH (CHỈ ĐỌC) ---
            HosoImageGridReadOnly(postImages)

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun HosoImageGridReadOnly(
    images: List<PostImageResponse>
) {
    Box(modifier = Modifier.height(500.dp)) {

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            items(images) { post ->

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(3f / 4f)
                        .clip(RoundedCornerShape(12.dp))
                ) {

                    AsyncImage(
                        model = post.img_url,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // ngày đăng giống hồ sơ chính
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .background(Color.Black.copy(0.5f))
                            .padding(2.dp)
                    ) {
                        Text(
                            text = post.created_at ?: "",
                            color = Color.White,
                            fontSize = 8.sp,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    }
}