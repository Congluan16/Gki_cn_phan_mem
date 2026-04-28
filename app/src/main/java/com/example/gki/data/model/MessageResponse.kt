package com.example.gki.data.model

import com.google.gson.annotations.SerializedName

data class MessageResponse(
    @SerializedName("id_message") val id_message: Int,
    @SerializedName("id_match") val id_match: Int,
    @SerializedName("sender_id") val sender_id: Int,
    @SerializedName("content") val content: String,
    @SerializedName("is_read") val is_read: Int,
    @SerializedName("timestamp") val timestamp: String
)