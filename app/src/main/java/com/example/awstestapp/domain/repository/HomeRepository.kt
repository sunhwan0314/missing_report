package com.example.awstestapp.domain.repository

import com.example.awstestapp.data.remote.dto.PostListItemDto

// 홈 화면에서 필요한 데이터 작업을 정의하는 인터페이스
interface HomeRepository {
    // 최신 실종자 목록 가져오기
    suspend fun getLatestMissingPersons(limit: Int): Result<List<PostListItemDto>>
    // 최신 실종 동물 목록 가져오기
    suspend fun getLatestMissingAnimals(limit: Int): Result<List<PostListItemDto>>
    suspend fun deletePost(postType: String, postId: Int, idToken: String): Result<Unit>
}