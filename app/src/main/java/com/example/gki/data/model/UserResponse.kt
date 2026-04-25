package com.example.gki.data.model

data class UserResponse(
    val id_user: Int,
    val full_name: String,
    val birth_date: String,
    val height: String,
    val weight: String,
    val hobbies: String,
    val img_url: String // Đường dẫn link ảnh từ bảng up_Img
)
