package com.example.awstestapp.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.awstestapp.data.remote.dto.CreatePersonRequestDto // 재사용
import com.example.awstestapp.data.remote.dto.PostDetailDto
import com.example.awstestapp.domain.repository.CreatePostRepository
import com.example.awstestapp.domain.repository.DetailRepository
import com.example.awstestapp.domain.repository.HomeRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// 수정 화면의 상태
data class EditPostUiState(
    val isLoading: Boolean = true,
    val post: PostDetailDto? = null,
    val isUpdateSuccess: Boolean = false,
    val errorMessage: String? = null
)

class EditPostViewModel(

    private val detailRepository: DetailRepository, // 기존 데이터 로드용
    private val createPostRepository: CreatePostRepository, // 데이터 업데이트용
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(EditPostUiState())
    val uiState = _uiState.asStateFlow()

    private val postId: Int = checkNotNull(savedStateHandle["postId"])
    private val postType: String = checkNotNull(savedStateHandle["postType"])

    init {
        // 화면이 열리자마자 기존 데이터를 불러옴
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            val result = if (postType == "person") detailRepository.getPersonDetail(postId) else detailRepository.getAnimalDetail(postId)
            result.onSuccess { postDetail ->
                _uiState.update { it.copy(isLoading = false, post = postDetail) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun updatePost(data: Any) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val idToken = auth.currentUser?.getIdToken(true)?.await()?.token
            if (idToken == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "인증 실패") }
                return@launch
            }
            createPostRepository.updatePost(idToken, postType, postId, data)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isUpdateSuccess = true) } }
                .onFailure { error -> _uiState.update { it.copy(isLoading = false, errorMessage = error.message) } }
        }
    }
}