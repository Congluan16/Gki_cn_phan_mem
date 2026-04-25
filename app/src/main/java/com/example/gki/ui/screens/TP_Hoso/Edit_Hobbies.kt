package com.example.gki.ui.screens.TP_Hoso

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.gki.ui.screens.VKU_Pink
import com.example.gki.ui.screens.AppBackground

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditHobbiesScreen(
    currentHobbies: String,
    onBack: () -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf(currentHobbies) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa sở thích", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
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
                .background(AppBackground)
                .padding(16.dp)
        ) {
            Text("Sở thích của bạn:", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                placeholder = { Text("Ví dụ: Đá bóng, lập trình, học luật...") },
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onSave(text) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = VKU_Pink),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Lưu thay đổi", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}