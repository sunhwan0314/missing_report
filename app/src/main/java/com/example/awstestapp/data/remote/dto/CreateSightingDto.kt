package com.example.awstestapp.data.remote.dto

data class CreateSightingRequestDto(
    val sighting_at: String,
    val sighting_location: String,
    val description: String?,
    val sighting_photo_url: String?
)