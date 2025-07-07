package com.example.awstestapp.data.remote.dto

data class SightingDto(
    val id: Int,
    val type: String, // "person" 또는 "animal"
    val name: String?,
    val sighting_location: String,
    val sighting_at: String,
    val sighting_photo_url: String?,
    val created_at: String,
    val description: String?,
    val reporter_nickname: String? // 제보자 닉네임 필드
)