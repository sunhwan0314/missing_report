package com.example.awstestapp.domain.repository

import com.example.awstestapp.data.remote.dto.UserDto

interface UserRepository {
    // Firebase 로그인 성공 후, 우리 서버에 해당 유저가 있는지 확인하는 기능
    suspend fun checkUserRegistered(idToken: String): Result<Boolean>
    // ... 앞으로 추가될 다른 사용자 관련 기능들 ...
    suspend fun getMyProfile(idToken: String): Result<UserDto>
}