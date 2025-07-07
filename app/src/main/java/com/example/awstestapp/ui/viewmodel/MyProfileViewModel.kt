package com.example.awstestapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.awstestapp.data.remote.dto.UserDto
import com.example.awstestapp.data.remote.dto.PostListItemDto
import com.example.awstestapp.domain.repository.MyProfileRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MyProfileUiState(
    val isLoading: Boolean = true,
    val user: UserDto? = null,
    val myPosts: List<PostListItemDto> = emptyList(),
    val errorMessage: String? = null
)

class MyProfileViewModel(
    private val myProfileRepository: MyProfileRepository,
    private val auth: FirebaseAuth
) : ViewModel() {
    private val _uiState = MutableStateFlow(MyProfileUiState())
    val uiState = _uiState.asStateFlow()

    fun fetchMyData() {
        auth.currentUser?.getIdToken(true)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val idToken = task.result?.token ?: ""
                viewModelScope.launch {
                    _uiState.update { it.copy(isLoading = true) }
                    // 프로필 정보와 내 게시물 목록을 동시에 요청
                    val profileResult = myProfileRepository.getMyProfile(idToken)
                    val postsResult = myProfileRepository.getMyPosts(idToken)

                    if(profileResult.isSuccess && postsResult.isSuccess) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                user = profileResult.getOrNull(),
                                myPosts = postsResult.getOrNull() ?: emptyList()
                            )
                        }
                    } else {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "데이터 로드 실패") }
                    }
                }
            }
        }
    }
}