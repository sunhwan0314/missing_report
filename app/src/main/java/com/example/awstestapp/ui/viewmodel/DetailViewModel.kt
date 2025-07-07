package com.example.awstestapp.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.awstestapp.data.remote.dto.PostDetailDto
import com.example.awstestapp.data.remote.dto.SightingDto
import com.example.awstestapp.domain.repository.DetailRepository
import com.example.awstestapp.domain.repository.HomeRepository
import com.example.awstestapp.domain.repository.UserRepository // 추가
import com.google.firebase.auth.FirebaseAuth // 추가
import kotlinx.coroutines.async // 추가
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.tasks.await

// UI 상태에 isOwner 플래그를 추가합니다.
data class DetailUiState(
    val isLoading: Boolean = true,
    val post: PostDetailDto? = null,
    val isOwner: Boolean = false,
    val sightings: List<SightingDto> = emptyList(),// <-- 이 게시물의 주인인지 여부
    val errorMessage: String? = null,
    val isPostDeleted: Boolean = false
)

class DetailViewModel(
    private val homeRepository: HomeRepository,
    private val userRepository: UserRepository, // UserRepository 주입
    private val auth: FirebaseAuth,         // FirebaseAuth 주입
    private val detailRepository: DetailRepository,

    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState = _uiState.asStateFlow()

    val postId: Int = checkNotNull(savedStateHandle["postId"])
    val postType: String = checkNotNull(savedStateHandle["postType"])

    init {
        fetchDetails()
    }

    private fun fetchDetails() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 1. 현재 사용자의 ID 토큰을 가져옵니다. (이제 .await()이 정상적으로 작동합니다)
            val idToken = auth.currentUser?.getIdToken(true)?.await()?.token
            if (idToken == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "로그인 정보가 없습니다.") }
                return@launch
            }

            // 2. '게시물 정보'와 '내 프로필 정보'를 동시에 요청합니다.
            val postDeferred = async {
                if (postType == "person") detailRepository.getPersonDetail(postId)
                else detailRepository.getAnimalDetail(postId)
            }
            val sightingsDeferred = async {
                if (postType == "person") detailRepository.getPersonSightings(postId)
                else detailRepository.getAnimalSightings(postId)
            }
            val myProfileDeferred = async { userRepository.getMyProfile(idToken) }

            val postResult = postDeferred.await()
            val myProfileResult = myProfileDeferred.await()
            val sightingsResult = sightingsDeferred.await()
            // 3. 두 요청이 모두 성공했는지 확인하고 상태를 업데이트합니다.
            if (postResult.isSuccess && myProfileResult.isSuccess&& sightingsResult.isSuccess) {
                val postDetail = postResult.getOrNull()
                val myProfile = myProfileResult.getOrNull()

                if (postDetail != null && myProfile != null) {
                    val isOwner = (postDetail.reporterId == myProfile.id || postDetail.ownerId == myProfile.id)
                    _uiState.update {
                        it.copy(isLoading = false, post = postDetail, isOwner = isOwner,sightings = sightingsResult.getOrNull() ?: emptyList())
                    }
                }
            } else {
                val errorMessage = postResult.exceptionOrNull()?.message ?: myProfileResult.exceptionOrNull()?.message
                _uiState.update { it.copy(isLoading = false, errorMessage = errorMessage ?: "알 수 없는 오류") }
            }
        }
    }
    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 1. 현재 사용자의 ID 토큰을 가져옵니다.
            val idToken = auth.currentUser?.getIdToken(true)?.await()?.token
            if (idToken == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "로그인 정보가 없습니다.") }
                return@launch
            }

            // 2. '게시물 정보', '목격담', '내 프로필 정보'를 동시에 요청하여 속도를 높입니다.
            val postDeferred = async { detailRepository.getPostDetail(postType, postId) }
            val sightingsDeferred = async { detailRepository.getSightings(postType, postId) }
            val myProfileDeferred = async { userRepository.getMyProfile(idToken) }

            val postResult = postDeferred.await()
            val sightingsResult = sightingsDeferred.await()
            val myProfileResult = myProfileDeferred.await()

            // 3. 모든 요청이 성공했는지 확인하고 상태를 업데이트합니다.
            if (postResult.isSuccess && myProfileResult.isSuccess && sightingsResult.isSuccess) {
                val postDetail = postResult.getOrNull()
                val myProfile = myProfileResult.getOrNull()

                if (postDetail != null && myProfile != null) {
                    val isOwner = (postDetail.reporterId == myProfile.id || postDetail.ownerId == myProfile.id)
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            post = postDetail,
                            sightings = sightingsResult.getOrNull() ?: emptyList(),
                            isOwner = isOwner
                        )
                    }
                }
            } else {
                // 여러 요청 중 하나라도 실패하면 에러 메시지를 표시합니다.
                val errorMessage = postResult.exceptionOrNull()?.message
                    ?: myProfileResult.exceptionOrNull()?.message
                    ?: sightingsResult.exceptionOrNull()?.message
                _uiState.update { it.copy(isLoading = false, errorMessage = errorMessage ?: "알 수 없는 오류") }
            }
        }
    }
    fun deletePost() {
        viewModelScope.launch {
            val idToken = auth.currentUser?.getIdToken(true)?.await()?.token ?: return@launch
            _uiState.update { it.copy(isLoading = true) }

            homeRepository.deletePost(postType, postId, idToken)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, isPostDeleted = true) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }
}

