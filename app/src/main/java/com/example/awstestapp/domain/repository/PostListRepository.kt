package com.example.awstestapp.domain.repository

import com.example.awstestapp.data.remote.dto.PostListItemDto

interface PostListRepository {
    // limit 없이 모든 데이터를 가져옵니다.
    suspend fun getAllMissingPersons(): Result<List<PostListItemDto>>
    suspend fun getAllMissingAnimals(): Result<List<PostListItemDto>>
}