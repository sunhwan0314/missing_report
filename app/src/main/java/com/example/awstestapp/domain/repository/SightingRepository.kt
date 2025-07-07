package com.example.awstestapp.domain.repository

import com.example.awstestapp.data.remote.dto.CreateSightingRequestDto

interface SightingRepository {
    suspend fun createSighting(
        idToken: String,
        postType: String,
        postId: Int,
        data: CreateSightingRequestDto
    ): Result<Unit>
}