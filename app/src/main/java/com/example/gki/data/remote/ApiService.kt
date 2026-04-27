package com.example.gki.data.remote

import com.example.gki.data.model.UserResponse
import com.example.gki.data.model.MatchResponse
import com.example.gki.data.model.PostImageResponse
import retrofit2.http.*

interface ApiService {
    @GET("get_user_profile.php")
    suspend fun getUserProfile(@Query("id") userId: Int): UserResponse

    @GET("get_all_users.php")
    suspend fun getAllUsers(): List<UserResponse>

    @FormUrlEncoded
    @POST("login.php")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): UserResponse

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

    @FormUrlEncoded
    @POST("upload_post_image.php")
    suspend fun uploadPostImage(
        @Field("id_user") userId: Int,
        @Field("image") imageBase64: String
    ): Map<String, String>

    @GET("get_post_images.php")
    suspend fun getPostImages(@Query("id_user") userId: Int): List<PostImageResponse>

    @GET("get_matches.php")
    suspend fun getMatches(@Query("my_id") myId: Int): List<MatchResponse>
    // Thêm vào interface ApiService
    @FormUrlEncoded
    @POST("delete_post_image.php")
    suspend fun deletePostImage(@Field("id_img") imageId: Int): Map<String, String>
    @FormUrlEncoded
    @POST("update_basic_info.php")
    suspend fun updateBasicInfo(
        @Field("id_user") userId: Int,
        @Field("birth_date") birthDate: String,
        @Field("height") height: String,
        @Field("weight") weight: String
    ): UserResponse
    // Thêm vào interface ApiService trong ApiService.kt
    @FormUrlEncoded
    @POST("manage_match.php")
    suspend fun manageMatch(
        @Field("action") action: String, // "send" hoặc "accept"
        @Field("my_id") myId: Int,
        @Field("target_id") targetId: Int
    ): Map<String, String>
}