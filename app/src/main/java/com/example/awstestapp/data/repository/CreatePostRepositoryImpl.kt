package com.example.awstestapp.data.repository

import com.example.awstestapp.data.remote.ApiService
import com.example.awstestapp.data.remote.dto.CreateAnimalRequestDto
import com.example.awstestapp.data.remote.dto.CreatePersonRequestDto
import com.example.awstestapp.domain.repository.CreatePostRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CreatePostRepositoryImpl(private val apiService: ApiService) : CreatePostRepository {
    override suspend fun createPersonPost(idToken: String, name: String, gender: String?, ageAtMissing: Int?, height: Int?, weight: Int?, lastSeenAt: String, lastSeenLocation: String, description: String?, photoUrl: String?): Result<Unit> = withContext(Dispatchers.IO) {
        val requestDto = CreatePersonRequestDto(
            missing_person_name = name,
            gender = gender,
            age_at_missing = ageAtMissing,
            height = height,
            weight = weight,
            last_seen_at = lastSeenAt,
            last_seen_location = lastSeenLocation,
            description = description,
            main_photo_url = photoUrl
        )
        try {
            apiService.createMissingPerson("Bearer $idToken", requestDto)
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }

    override suspend fun createAnimalPost(idToken: String, name: String?, animalType: String, breed: String?, gender: String?, age: Int?, lastSeenAt: String, lastSeenLocation: String, description: String?, photoUrl: String?): Result<Unit> = withContext(Dispatchers.IO) {
        val requestDto = CreateAnimalRequestDto(
            animal_name = name,
            animal_type = animalType,
            breed = breed,
            gender = gender,
            age = age,
            last_seen_at = lastSeenAt,
            last_seen_location = lastSeenLocation,
            description = description,
            main_photo_url = photoUrl
        )
        try {
            apiService.createMissingAnimal("Bearer $idToken", requestDto)
            Result.success(Unit)
        } catch (e: Exception) { Result.failure(e) }
    }
    override suspend fun <T : Any> updatePost(idToken: String, postType: String, postId: Int, postData: T): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val tokenHeader = "Bearer $idToken"
            when (postType) {
                "person" -> apiService.updatePersonDetail(tokenHeader, postId, postData as CreatePersonRequestDto)
                "animal" -> apiService.updateAnimalDetail(tokenHeader, postId, postData as CreateAnimalRequestDto)
                else -> throw IllegalArgumentException("Invalid post type")
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}