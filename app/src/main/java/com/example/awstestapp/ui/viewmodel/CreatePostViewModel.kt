package com.example.awstestapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.awstestapp.data.remote.dto.CreateAnimalRequestDto
import com.example.awstestapp.data.remote.dto.CreatePersonRequestDto
import com.example.awstestapp.domain.repository.CreatePostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseAuth

data class CreatePostUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class CreatePostViewModel(
    private val createPostRepository: CreatePostRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState = _uiState.asStateFlow()

    fun createPersonPost(data: CreatePersonRequestDto) {
        // Firebase에서 ID 토큰을 먼저 가져옵니다.
        auth.currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val idToken = task.result?.token
                if (idToken != null) {
                    // 토큰을 성공적으로 가져왔으면, Repository에 토큰과 함께 게시물 생성을 요청합니다.
                    viewModelScope.launch {
                        _uiState.update { it.copy(isLoading = true) }
                        createPostRepository.createPersonPost(idToken, data)
                            .onSuccess { _uiState.update { it.copy(isLoading = false, isSuccess = true) } }
                            .onFailure { error -> _uiState.update { it.copy(isLoading = false, errorMessage = error.message) } }
                    }
                } else {
                    _uiState.update { it.copy(errorMessage = "인증 토큰을 가져오는데 실패했습니다.") }
                }
            } else {
                _uiState.update { it.copy(errorMessage = "인증 정보를 가져오는데 실패했습니다.") }
            }
        }
    }
    // ... (동물 등록 함수, errorShown 함수 등은 동일하게 구현)
}
