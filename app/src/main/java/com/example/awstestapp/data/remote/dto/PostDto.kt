package com.example.awstestapp.data.remote.dto

import com.google.gson.annotations.SerializedName

// 서버의 모든 '목록' API 응답을 처리할 수 있는 최종 데이터 모델
data class PostListItemDto(
    val id: Int,
    val type: String?, // "person" 또는 "animal"

    // [수정] 서버의 personName, animalName 키와 정확히 일치시킴
    @SerializedName("personName")
    val personName: String?,

    @SerializedName("animalName")
    val animalName: String?,

    // age_at_missing, breed 등 다른 필드들은 상세보기에만 필요하므로,
    // 목록 DTO에서는 제외하거나, 서버 응답에 맞춰 추가할 수 있습니다.
    // 지금은 목록 표시에 필요한 최소한의 정보만 포함합니다.
    val last_seen_location: String,
    val main_photo_url: String?,
    val created_at: String
)