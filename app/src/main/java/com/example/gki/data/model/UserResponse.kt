package com.example.gki.data.model

import com.google.gson.annotations.SerializedName

data class  UserResponse(
    val id_user: Int,
    val full_name: String?,
    val birth_date: String?,
    val height: String?,
    val weight: String?,
    val hobbies: String?,

    // Đã chuyển sang String để lưu link ảnh trực tiếp
    @SerializedName("profile_img_id")
    val profile_img_id: String?,

    val img_url: String?
)