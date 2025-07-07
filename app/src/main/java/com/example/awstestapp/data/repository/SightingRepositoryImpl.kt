package com.example.awstestapp.data.repository

import com.example.awstestapp.data.remote.ApiService
import com.example.awstestapp.data.remote.dto.CreateSightingRequestDto
import com.example.awstestapp.domain.repository.SightingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SightingRepositoryImpl(private val apiService: ApiService) : SightingRepository {
    override suspend fun createSighting(
        idToken: String,
        postType: String,
        postId: Int,
        data: CreateSightingRequestDto
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val tokenHeader = "Bearer $idToken"
            if (postType == "person") {
                apiService.createPersonSighting(tokenHeader, postId, data)
            } else {
                apiService.createAnimalSighting(tokenHeader, postId, data)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}