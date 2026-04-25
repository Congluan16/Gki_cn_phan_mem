package com.example.gki.ui.screens.TP_Hoso

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
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
import coil.compose.AsyncImage
import com.example.gki.R
import com.example.gki.ui.screens.VKU_Pink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditAvataScreen(
    currentName: String,
    currentImageUrl: String?,
    onBack: () -> Unit,
    onSave: (String, Uri?) -> Unit // Đảm bảo truyền đủ dữ liệu ra ngoài
) {
    var name by remember { mutableStateOf(currentName) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa thông tin", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    TextButton(onClick = { 
                        if (name.isNotEmpty()) {
                            onSave(name, selectedImageUri)
                        }
                    }) {
                        Text("Lưu", color = VKU_Pink, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Họ và tên") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier.size(180.dp)
            ) {
                AsyncImage(
                    model = when {
                        selectedImageUri != null -> selectedImageUri
                        currentImageUrl.isNullOrEmpty() -> R.drawable.dai_dien
                        else -> currentImageUrl
                    },
                    contentDescription = "Avatar",
                    placeholder = painterResource(R.drawable.dai_dien),
                    error = painterResource(R.drawable.dai_dien),
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(4.dp, VKU_Pink, CircleShape),
                    contentScale = ContentScale.Crop
                )

                SmallFloatingActionButton(
                    onClick = {
                        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    },
                    containerColor = VKU_Pink,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.offset(x = (-8).dp, y = (-8).dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Image", modifier = Modifier.size(20.dp))
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Nhấn vào biểu tượng cây bút để thay đổi ảnh đại diện",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
    }
}
