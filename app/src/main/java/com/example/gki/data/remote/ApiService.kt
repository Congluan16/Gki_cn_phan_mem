package com.example.gki.data.remote

import com.example.gki.data.model.UserResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @GET("get_user_profile.php")
    suspend fun getUserProfile(@Query("id") userId: Int): UserResponse

    @FormUrlEncoded
    @POST("update_hobbies.php")
    suspend fun updateHobbies(
        @Field("id_user") userId: Int,
        @Field("hobbies") hobbies: String
    ): UserResponse

    @FormUrlEncoded
    @POST("update_profile.php")
    suspend fun updateProfile(
        @Field("id_user") userId: Int,
        @Field("full_name") fullName: String
    ): UserResponse

    @FormUrlEncoded
    @POST("upload_avatar.php")
    suspend fun updateProfileImage(
        @Field("id_user") userId: Int,
        @Field("image") imageBase64: String
    ): UserResponse
}