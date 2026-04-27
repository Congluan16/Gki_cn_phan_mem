package com.example.gki.ui.screens.TP_Hoso

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gki.data.model.UserResponse

@Composable
fun EditInfoScreen(
    currentUser: UserResponse?,
    onBack: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var birth by remember { mutableStateOf(currentUser?.birth_date ?: "") }
    var height by remember { mutableStateOf(currentUser?.height ?: "") }
    var weight by remember { mutableStateOf(currentUser?.weight ?: "") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Chỉnh sửa thông tin cơ bản", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(value = birth, onValueChange = { birth = it }, label = { Text("Ngày sinh (yyyy-mm-dd)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = height, onValueChange = { height = it }, label = { Text("Chiều cao (cm)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(value = weight, onValueChange = { weight = it }, label = { Text("Cân nặng (kg)") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = { onSave(birth, height, weight) }, modifier = Modifier.fillMaxWidth()) {
            Text("Lưu thay đổi")
        }
        TextButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Hủy")
        }
    }
}