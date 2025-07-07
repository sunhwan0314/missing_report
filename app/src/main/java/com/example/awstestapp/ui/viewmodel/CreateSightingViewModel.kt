package com.example.awstestapp.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.awstestapp.data.remote.dto.CreateSightingRequestDto
import com.example.awstestapp.domain.repository.SightingRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class CreateSightingUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

class CreateSightingViewModel(
    private val sightingRepository: SightingRepository,
    private val auth: FirebaseAuth,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateSightingUiState())
    val uiState = _uiState.asStateFlow()

    private val postId: Int = checkNotNull(savedStateHandle["postId"])
    private val postType: String = checkNotNull(savedStateHandle["postType"])

    fun createSighting(data: CreateSightingRequestDto) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val idToken = auth.currentUser?.getIdToken(true)?.await()?.token
            if (idToken == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "인증 실패") }
                return@launch
            }
            sightingRepository.createSighting(idToken, postType, postId, data)
                .onSuccess { _uiState.update { it.copy(isLoading = false, isSuccess = true) } }
                .onFailure { error -> _uiState.update { it.copy(isLoading = false, errorMessage = error.message) } }
        }
    }
}