package com.example.gki.ui.screens.TP_Hoso

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gki.ui.screens.VKU_Pink

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpImgScreen(
    onBack: () -> Unit,
    onUpload: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Đăng ảnh mới") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Khung giả lập hiển thị ảnh sắp đăng
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color.LightGray, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Chưa chọn ảnh", color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onUpload,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = VKU_Pink)
            ) {
                Text("Chọn ảnh và Tải lên")
            }
        }
    }
}