package com.example.awstestapp.data.repository

import com.example.awstestapp.data.remote.ApiService
import com.example.awstestapp.data.remote.dto.UserDto
import com.example.awstestapp.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepositoryImpl(
    private val apiService: ApiService // 이제 OkHttpClient 대신 ApiService를 주입받음
) : UserRepository {

    override suspend fun checkUserRegistered(idToken: String): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            // Retrofit으로 훨씬 더 깔끔하게 API를 호출합니다.
            apiService.getMyProfile("Bearer $idToken")
            // 여기서 코드가 성공적으로 실행되었다는 것은, 서버 응답이 200 OK 라는 의미입니다.
            Result.success(true)
        } catch (e: Exception) {
            // Retrofit이 HTTP 에러를 Exception으로 던져줍니다.
            // 여기서는 404 Not Found 에러인지 확인하여 분기 처리합니다.
            if (e is retrofit2.HttpException && e.code() == 404) {
                Result.success(false) // 404 에러는 우리 로직상 '성공'(미가입자)
            } else {
                Result.failure(e) // 그 외 모든 네트워크 에러는 '실패'
            }
        }
    }
    override suspend fun getMyProfile(idToken: String): Result<UserDto> = withContext(Dispatchers.IO) {
        try {
            // Retrofit을 통해 서버 API를 호출합니다.
            val userProfile = apiService.getMyProfile("Bearer $idToken")
            Result.success(userProfile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}