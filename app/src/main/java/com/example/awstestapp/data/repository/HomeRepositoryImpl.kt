package com.example.awstestapp.data.repository

import com.example.awstestapp.data.remote.ApiService
import com.example.awstestapp.data.remote.dto.PostDetailDto
import com.example.awstestapp.domain.repository.HomeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeRepositoryImpl(private val apiService: ApiService) : HomeRepository {

    override suspend fun getLatestMissingPersons(limit: Int) = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getMissingPersons(limit = limit)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLatestMissingAnimals(limit: Int) = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getMissingAnimals(limit = limit)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getPersonDetail(postId: Int): Result<PostDetailDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getPersonDetail(postId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAnimalDetail(postId: Int): Result<PostDetailDto> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getAnimalDetail(postId)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun deletePost(postType: String, postId: Int, idToken: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val tokenHeader = "Bearer $idToken"
            if (postType == "person") {
                apiService.deletePersonDetail(tokenHeader, postId)
            } else {
                apiService.deleteAnimalDetail(tokenHeader, postId)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

}