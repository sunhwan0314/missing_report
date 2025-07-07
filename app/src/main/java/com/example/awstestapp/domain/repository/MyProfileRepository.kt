package com.example.awstestapp.domain.repository
import com.example.awstestapp.data.remote.dto.PostListItemDto
import com.example.awstestapp.data.remote.dto.UserDto

interface MyProfileRepository {
    suspend fun getMyProfile(idToken: String): Result<UserDto>
    suspend fun getMyPosts(idToken: String): Result<List<PostListItemDto>>
}