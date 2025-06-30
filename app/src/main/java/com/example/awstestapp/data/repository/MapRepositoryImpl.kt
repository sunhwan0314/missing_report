package com.example.awstestapp.data.repository

import com.example.awstestapp.data.remote.ApiService
import com.example.awstestapp.domain.repository.MapRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MapRepositoryImpl(private val apiService: ApiService) : MapRepository {
    override suspend fun getAllSightings() = withContext(Dispatchers.IO) {
        try {
            Result.success(apiService.getAllSightings())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}