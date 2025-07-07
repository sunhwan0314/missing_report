package com.example.awstestapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.awstestapp.data.remote.dto.PostListItemDto
import com.example.awstestapp.domain.repository.PostListRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PostListUiState(
    val isLoading: Boolean = true,
    val title: String = "",
    val posts: List<PostListItemDto> = emptyList(),
    val errorMessage: String? = null
)

class PostListViewModel(
    private val postListRepository: PostListRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PostListUiState())
    val uiState = _uiState.asStateFlow()

    fun loadPosts(postType: String) {
        viewModelScope.launch {
            val title = if (postType == "person") "전체 실종자 목록" else "전체 실종 동물 목록"
            _uiState.update { it.copy(isLoading = true, title = title) }

            val result = if (postType == "person") {
                postListRepository.getAllMissingPersons()
            } else {
                postListRepository.getAllMissingAnimals()
            }

            result.onSuccess { posts ->
                _uiState.update { it.copy(isLoading = false, posts = posts) }
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }
}