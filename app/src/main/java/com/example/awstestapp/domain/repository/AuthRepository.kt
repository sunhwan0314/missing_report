package com.example.awstestapp.domain.repository

interface AuthRepository {
    suspend fun registerUser(
        firebaseUid: String,
        phoneNumber: String,
        realName: String,
        nickname: String,
        ci: String
    ): Result<Unit>
}