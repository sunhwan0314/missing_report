package com.example.awstestapp.data.repository

import com.example.awstestapp.data.remote.ApiService
import com.example.awstestapp.domain.repository.MyProfileRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


class MyProfileRepositoryImpl(private val apiService: ApiService) : MyProfileRepository {
    override suspend fun getMyProfile(idToken: String) = withContext(Dispatchers.IO) {
        try { Result.success(apiService.getMyProfile("Bearer $idToken")) }
        catch (e: Exception) { Result.failure(e) }
    }
    override suspend fun getMyPosts(idToken: String) = withContext(Dispatchers.IO) {
        try { Result.success(apiService.getMyPosts("Bearer $idToken")) }
        catch (e: Exception) { Result.failure(e) }
    }
}