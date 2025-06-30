package com.example.awstestapp.data.remote.dto

data class RegisterRequestDto(
    val firebase_uid: String,
    val phone_number: String,
    val real_name: String,
    val nickname: String,
    val ci: String
)