package com.example.awstestapp.data.repository

import com.example.awstestapp.data.remote.ApiService
import com.example.awstestapp.data.remote.dto.CreateAnimalRequestDto
import com.example.awstestapp.data.remote.dto.CreatePersonRequestDto
import com.example.awstestapp.domain.repository.CreatePostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CreatePostRepositoryImpl(private val apiService: ApiService) : CreatePostRepository {
    override suspend fun createPersonPost(idToken: String, data: CreatePersonRequestDto): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            apiService.createMissingPerson("Bearer $idToken", data)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createAnimalPost(idToken: String, data: CreateAnimalRequestDto): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            apiService.createMissingAnimal("Bearer $idToken", data)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun <T : Any> updatePost(idToken: String, postType: String, postId: Int, postData: T): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val tokenHeader = "Bearer $idToken"
            when (postType) {
                "person" -> apiService.updatePersonDetail(tokenHeader, postId, postData as CreatePersonRequestDto)
                "animal" -> apiService.updateAnimalDetail(tokenHeader, postId, postData as CreateAnimalRequestDto)
                else -> throw IllegalArgumentException("Invalid post type")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}