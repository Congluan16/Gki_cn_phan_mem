package com.example.gki.data.model

data class PostImageResponse(
    val id_img: Int, // Thêm ID để biết ảnh nào cần xóa
    val img_url: String,
    val created_at: String?
)