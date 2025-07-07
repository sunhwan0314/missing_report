package com.example.awstestapp.data.repository

import com.example.awstestapp.data.remote.ApiService
import com.example.awstestapp.domain.repository.DetailRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DetailRepositoryImpl(
    private val apiService: ApiService
) : DetailRepository {

    override suspend fun getPersonDetail(postId: Int) = withContext(Dispatchers.IO) {
        try {
            Result.success(apiService.getPersonDetail(postId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAnimalDetail(postId: Int) = withContext(Dispatchers.IO) {
        try {
            Result.success(apiService.getAnimalDetail(postId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPersonSightings(postId: Int) = withContext(Dispatchers.IO) {
        try {
            Result.success(apiService.getPersonSightings(postId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAnimalSightings(postId: Int) = withContext(Dispatchers.IO) {
        try {
            Result.success(apiService.getAnimalSightings(postId))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun getPostDetail(postType: String, postId: Int) = withContext(Dispatchers.IO) {
        try {
            // postType에 따라 다른 API를 호출합니다.
            val response = if (postType == "person") {
                apiService.getPersonDetail(postId)
            } else {
                apiService.getAnimalDetail(postId)
            }
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSightings(postType: String, postId: Int) = withContext(Dispatchers.IO) {
        try {
            // postType에 따라 다른 API를 호출합니다.
            val response = if (postType == "person") {
                apiService.getPersonSightings(postId)
            } else {
                apiService.getAnimalSightings(postId)
            }
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}