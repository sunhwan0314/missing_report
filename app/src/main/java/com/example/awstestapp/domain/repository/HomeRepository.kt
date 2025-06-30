package com.example.awstestapp.domain.repository

import com.example.awstestapp.data.remote.dto.PostDetailDto
import com.example.awstestapp.data.remote.dto.PostListItemDto

// 홈 화면에서 필요한 데이터 작업을 정의하는 인터페이스
interface HomeRepository {
    suspend fun getLatestMissingPersons(limit: Int): Result<List<PostListItemDto>>
    suspend fun getLatestMissingAnimals(limit: Int): Result<List<PostListItemDto>>
    suspend fun getPersonDetail(postId: Int): Result<PostDetailDto>
    suspend fun getAnimalDetail(postId: Int): Result<PostDetailDto>
    suspend fun deletePost(postType: String, postId: Int, idToken: String): Result<Unit>

}