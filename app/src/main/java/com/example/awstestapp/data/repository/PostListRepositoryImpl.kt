package com.example.awstestapp.data.repository

import com.example.awstestapp.data.remote.ApiService
import com.example.awstestapp.domain.repository.PostListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PostListRepositoryImpl(private val apiService: ApiService) : PostListRepository {
    override suspend fun getAllMissingPersons() = withContext(Dispatchers.IO) {
        try {
            // ApiService의 limit 파라미터에 null을 전달하여 전체 목록을 요청합니다.
            Result.success(apiService.getMissingPersons(limit = null))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllMissingAnimals() = withContext(Dispatchers.IO) {
        try {
            Result.success(apiService.getMissingAnimals(limit = null))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}