package com.example.awstestapp.ui.viewmodel

import android.net.Uri
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.awstestapp.data.remote.dto.CreateAnimalRequestDto
import com.example.awstestapp.data.remote.dto.CreatePersonRequestDto
import com.example.awstestapp.domain.repository.CreatePostRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// 게시물 종류를 나타내는 Enum 클래스
enum class PostType {
    PERSON, ANIMAL
}

// 글쓰기 화면의 모든 UI 상태를 관리
data class CreatePostUiState(
    val postType: PostType = PostType.PERSON,
    val isLoading: Boolean = false,
    val isPostCreated: Boolean = false,
    val errorMessage: String? = null,

    // 공통 필드
    val name: String = "",
    val gender: String = "",
    val lastSeenAt: String = "",
    val lastSeenLocation: String = "",
    val description: String = "",
    val photoUri: String? = null,
    val isGenderDropdownExpanded: Boolean = false,
    // 사람 전용 필드
    val ageAtMissing: String = "",
    val height: String = "",
    val weight: String = "",

    // 동물 전용 필드
    val animalType: String = "",
    val breed: String = "",
    val age: String = ""
)

class CreatePostViewModel(
    private val createPostRepository: CreatePostRepository,
    private val auth: FirebaseAuth,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState = _uiState.asStateFlow()

    // --- 이벤트 핸들러 함수들 (전체 구현) ---
    fun onGenderDropdownDismiss() {
        _uiState.update { it.copy(isGenderDropdownExpanded = false) }
    }
    fun onGenderDropdownClick() {
        _uiState.update { it.copy(isGenderDropdownExpanded = true) }
    }
    fun onPhotoSelected(uri: String?) {
        _uiState.update { it.copy(photoUri = uri) }
    }
    fun onPostTypeChange(newType: PostType) = _uiState.update { it.copy(postType = newType) }
    fun onNameChange(newName: String) = _uiState.update { it.copy(name = newName) }
    fun onGenderChange(newGender: String) = _uiState.update { it.copy(gender = newGender, isGenderDropdownExpanded = false) }
    fun onLastSeenAtChange(newTime: String) = _uiState.update { it.copy(lastSeenAt = newTime) }
    fun onLastSeenLocationChange(newLocation: String) = _uiState.update { it.copy(lastSeenLocation = newLocation) }
    fun onDescriptionChange(newDescription: String) = _uiState.update { it.copy(description = newDescription) }
    fun onAgeAtMissingChange(newAge: String) = _uiState.update { it.copy(ageAtMissing = newAge) }
    fun onHeightChange(newHeight: String) = _uiState.update { it.copy(height = newHeight) }
    fun onWeightChange(newWeight: String) = _uiState.update { it.copy(weight = newWeight) }
    fun onAnimalTypeChange(newType: String) = _uiState.update { it.copy(animalType = newType) }
    fun onBreedChange(newBreed: String) = _uiState.update { it.copy(breed = newBreed) }
    fun onAgeChange(newAge: String) = _uiState.update { it.copy(age = newAge) }
    fun onErrorShown() = _uiState.update { it.copy(errorMessage = null) }


    // '작성 완료' 버튼 클릭 시 호출될 메인 함수
    fun submitPost() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val imageUrl = if (_uiState.value.photoUri != null) {
                uploadImage(_uiState.value.photoUri!!.toUri())
            } else {
                null
            }
            createPost(imageUrl)
        }
    }

    private suspend fun uploadImage(imageUri: Uri): String? {
        return try {
            val user = auth.currentUser ?: throw Exception("로그인이 필요합니다.")
            val storageRef = storage.reference.child("images/${user.uid}/${System.currentTimeMillis()}.jpg")
            val uploadTask = storageRef.putFile(imageUri).await()
            uploadTask.storage.downloadUrl.await().toString()
        } catch (e: Exception) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "이미지 업로드 실패: ${e.message}") }
            null
        }
    }

    private suspend fun createPost(imageUrl: String?) {
        val idToken = auth.currentUser?.getIdToken(true)?.await()?.token
        if (idToken == null) {
            _uiState.update { it.copy(isLoading = false, errorMessage = "인증 실패") }
            return
        }

        val currentState = _uiState.value
        // [수정] Repository 함수에 모든 파라미터를 개별적으로 전달합니다.
        val result = if (currentState.postType == PostType.PERSON) {
            createPostRepository.createPersonPost(
                idToken = idToken,
                name = currentState.name,
                gender = currentState.gender.ifBlank { null },
                ageAtMissing = currentState.ageAtMissing.toIntOrNull(),
                height = currentState.height.toIntOrNull(),
                weight = currentState.weight.toIntOrNull(),
                lastSeenAt = currentState.lastSeenAt,
                lastSeenLocation = currentState.lastSeenLocation,
                description = currentState.description.ifBlank { null },
                photoUrl = imageUrl
            )
        } else {
            createPostRepository.createAnimalPost(
                idToken = idToken,
                name = currentState.name.ifBlank { null },
                animalType = currentState.animalType,
                breed = currentState.breed.ifBlank { null },
                gender = currentState.gender.ifBlank { null },
                age = currentState.age.toIntOrNull(),
                lastSeenAt = currentState.lastSeenAt,
                lastSeenLocation = currentState.lastSeenLocation,
                description = currentState.description.ifBlank { null },
                photoUrl = imageUrl
            )
        }

        result.onSuccess {
            _uiState.update { it.copy(isLoading = false, isPostCreated = true) }
        }.onFailure { error ->
            _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
        }
    }
}