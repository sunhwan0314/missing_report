package com.example.awstestapp.data.remote.dto

// /api/users/me API의 응답을 위한 데이터 클래스
data class UserDto(
    val id: Int,
    val firebase_uid: String,
    val phone_number: String?,
    val real_name: String,
    val nickname: String,
    val profile_image_url: String?,
    val created_at: String
)