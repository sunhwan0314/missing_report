package com.example.awstestapp.data.remote.dto

data class SightingDto(
    val id: Int,
    val type: String, // "person" 또는 "animal"
    val name: String?,
    val sighting_location: String,
    val sighting_at: String
)