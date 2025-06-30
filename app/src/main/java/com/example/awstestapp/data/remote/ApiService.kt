package com.example.awstestapp.data.remote

import com.example.awstestapp.data.remote.dto.CreateAnimalRequestDto
import com.example.awstestapp.data.remote.dto.CreatePersonRequestDto
import com.example.awstestapp.data.remote.dto.RegisterRequestDto
import com.example.awstestapp.data.remote.dto.PostListItemDto
import com.example.awstestapp.data.remote.dto.UserDto // UserDto import 추가
import retrofit2.http.GET
import retrofit2.http.Header // Header import 추가
import retrofit2.http.Query
import retrofit2.http.Body
import retrofit2.http.POST
import com.example.awstestapp.data.remote.dto.PostDetailDto // 추가
import com.example.awstestapp.data.remote.dto.SightingDto
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path // 추가

interface ApiService {
    // [새로 추가] 내 정보 조회 API
    @GET("/api/users/me")
    suspend fun getMyProfile(
        @Header("Authorization") idToken: String // HTTP 헤더에 토큰을 추가
    ): UserDto // 응답으로 UserDto 객체를 받음

    // 최신 실종자 목록을 가져오는 API
    @GET("/api/missing-persons")
    suspend fun getMissingPersons(
        @Query("limit") limit: Int
    ): List<PostListItemDto>

    // 최신 실종 동물 목록을 가져오는 API
    @GET("/api/missing-animals")
    suspend fun getMissingAnimals(
        @Query("limit") limit: Int
    ): List<PostListItemDto>
    @POST("/api/auth/register")
    suspend fun registerUser(@Body requestBody: RegisterRequestDto)
    // [새로 추가] 특정 실종자 상세 정보 조회
    @GET("/api/missing-persons/{id}")
    suspend fun getPersonDetail(
        @Path("id") postId: Int // URL 경로의 :id 부분을 채워줌
    ): PostDetailDto

    // [새로 추가] 특정 실종 동물 상세 정보 조회
    @GET("/api/missing-animals/{id}")
    suspend fun getAnimalDetail(
        @Path("id") postId: Int
    ): PostDetailDto
    @POST("/api/missing-persons")
    suspend fun createMissingPerson(@Header("Authorization") token: String, @Body request: CreatePersonRequestDto)

    @POST("/api/missing-animals")
    suspend fun createMissingAnimal(@Header("Authorization") token: String, @Body request: CreateAnimalRequestDto)

    @DELETE("/api/missing-persons/{id}")
    suspend fun deletePersonDetail(@Header("Authorization") token: String, @Path("id") postId: Int)

    @DELETE("/api/missing-animals/{id}")
    suspend fun deleteAnimalDetail(@Header("Authorization") token: String, @Path("id") postId: Int)


    @PATCH("/api/missing-persons/{id}")
    suspend fun updatePersonDetail(@Header("Authorization") token: String, @Path("id") postId: Int, @Body request: CreatePersonRequestDto)

    @PATCH("/api/missing-animals/{id}")
    suspend fun updateAnimalDetail(@Header("Authorization") token: String, @Path("id") postId: Int, @Body request: CreateAnimalRequestDto)
    // [새로 추가] 모든 목격담 정보 조회
    @GET("/api/sightings/all")
    suspend fun getAllSightings(): List<SightingDto>

}