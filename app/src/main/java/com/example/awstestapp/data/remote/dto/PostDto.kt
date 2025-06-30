package com.example.awstestapp.data.remote.dto

import com.google.gson.annotations.SerializedName

// 서버의 /api/missing-persons, /api/missing-animals 목록 API 응답을 위한 데이터 클래스
data class PostListItemDto(
    val id: Int,
    @SerializedName("missing_person_name") // JSON의 키 이름과 변수 이름이 다를 때 사용
    val personName: String?,
    @SerializedName("animal_name")
    val animalName: String?,
    @SerializedName("age_at_missing")
    val age: Int?,
    val breed: String?,
    val last_seen_location: String,
    val main_photo_url: String?,
    val created_at: String
)