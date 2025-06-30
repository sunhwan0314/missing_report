package com.example.awstestapp.domain.repository

// DTO import가 필요할 수 있습니다.
import com.example.awstestapp.data.remote.dto.CreateAnimalRequestDto
import com.example.awstestapp.data.remote.dto.CreatePersonRequestDto

interface CreatePostRepository {
    // 파라미터로 idToken을 받도록 수정
    suspend fun createPersonPost(idToken: String, data: CreatePersonRequestDto): Result<Unit>
    suspend fun createAnimalPost(idToken: String, data: CreateAnimalRequestDto): Result<Unit>
    suspend fun <T : Any> updatePost(idToken: String, postType: String, postId: Int, postData: T): Result<Unit>

}