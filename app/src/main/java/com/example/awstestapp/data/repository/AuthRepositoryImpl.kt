package com.example.awstestapp.data.repository

import com.example.awstestapp.data.remote.ApiService
import com.example.awstestapp.data.remote.dto.RegisterRequestDto
import com.example.awstestapp.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepositoryImpl(private val apiService: ApiService) : AuthRepository {
    override suspend fun registerUser(
        firebaseUid: String,
        phoneNumber: String,
        realName: String,
        nickname: String,
        ci: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val requestBody = RegisterRequestDto(firebaseUid, phoneNumber, realName, nickname, ci)
            apiService.registerUser(requestBody)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}