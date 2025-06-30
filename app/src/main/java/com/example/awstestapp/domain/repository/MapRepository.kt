package com.example.awstestapp.domain.repository

import com.example.awstestapp.data.remote.dto.SightingDto

interface MapRepository {
    suspend fun getAllSightings(): Result<List<SightingDto>>
}