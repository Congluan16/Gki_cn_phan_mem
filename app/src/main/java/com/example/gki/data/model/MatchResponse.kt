package com.example.gki.data.model
import com.google.gson.annotations.SerializedName

data class MatchResponse(
    @SerializedName("user_one_id") val user_one_id: Int?,
    @SerializedName("user_two_id") val user_two_id: Int?,
    @SerializedName("sender_id") val sender_id: Int?,
    @SerializedName("status") val status: Int? // 0: Chờ, 1: Bạn bè
)