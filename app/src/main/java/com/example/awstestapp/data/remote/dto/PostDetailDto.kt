package com.example.awstestapp.data.remote.dto

import com.google.gson.annotations.SerializedName

// 상세 페이지의 모든 정보를 담을 수 있는 최종 데이터 모델
data class PostDetailDto(
    val id: Int,
    @SerializedName("reporter_id")
    val reporterId: Int?,
    @SerializedName("owner_id")
    val ownerId: Int?,
    val status: String?,

    // --- 사람 정보 ---
    @SerializedName("missing_person_name")
    val personName: String?,
    @SerializedName("age_at_missing")
    val ageAtMissing: Int?,
    val height: Int?,
    val weight: Int?,

    // --- 동물 정보 ---
    @SerializedName("animal_name")
    val animalName: String?,
    val animal_type: String?,
    val breed: String?,
    val age: Int?,

    // --- 공통 정보 ---
    val gender: String?,
    val last_seen_at: String,
    val last_seen_location: String,
    val description: String?,
    val main_photo_url: String?,
    val created_at: String,
    val updated_at: String
)