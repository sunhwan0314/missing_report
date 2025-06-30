package com.example.awstestapp.data.remote.dto

// 사람 등록 요청 DTO
data class CreatePersonRequestDto(
    val missing_person_name: String,
    val gender: String?,
    val age_at_missing: Int?,
    val height: Int?,
    val weight: Int?,
    val last_seen_at: String,
    val last_seen_location: String,
    val description: String?,
    val main_photo_url: String?
)

// 동물 등록 요청 DTO
data class CreateAnimalRequestDto(
    val animal_type: String,
    val breed: String?,
    val animal_name: String?,
    val gender: String?,
    val age: Int?,
    val last_seen_at: String,
    val last_seen_location: String,
    val description: String?,
    val main_photo_url: String?
)