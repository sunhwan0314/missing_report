package com.example.awstestapp.domain.repository

import com.example.awstestapp.data.remote.dto.PostDetailDto
import com.example.awstestapp.data.remote.dto.SightingDto

interface DetailRepository {
    // 특정 실종자 상세 정보 가져오기
    suspend fun getPersonDetail(postId: Int): Result<PostDetailDto>
    // 특정 실종 동물의 상세 정보 가져오기
    suspend fun getAnimalDetail(postId: Int): Result<PostDetailDto>

    // 특정 실종자의 목격담 목록 가져오기
    suspend fun getPersonSightings(postId: Int): Result<List<SightingDto>>
    // 특정 실종 동물의 목격담 목록 가져오기
    suspend fun getAnimalSightings(postId: Int): Result<List<SightingDto>>
    suspend fun getPostDetail(postType: String, postId: Int): Result<PostDetailDto>

    // 게시물 타입과 ID에 따라 목격담 목록을 가져오는 기능
    suspend fun getSightings(postType: String, postId: Int): Result<List<SightingDto>>
}