package com.example.awstestapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.awstestapp.domain.repository.AuthRepository // 나중에 만들 AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 회원가입 화면의 UI 상태
data class RegisterUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRegistrationSuccess: Boolean = false
)

class RegisterViewModel(
    private val auth: FirebaseAuth,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState = _uiState.asStateFlow()

    // 최종 회원가입을 서버에 요청하는 함수
    fun registerUser(nickname: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Firebase 사용자를 찾을 수 없습니다. 다시 로그인해주세요.") }
                return@launch
            }

            // TODO: 실제 앱에서는 통신사 본인확인 모듈에서 실명과 CI 값을 가져와야 합니다.
            val realName = "김테스트" // 임시 실명
            val ci = "ci_kotlin_test_${System.currentTimeMillis()}" // 임시 고유값

            // Repository를 통해 서버에 회원가입을 요청합니다.
            authRepository.registerUser(
                firebaseUid = firebaseUser.uid,
                phoneNumber = firebaseUser.phoneNumber!!,
                realName = realName,
                nickname = nickname,
                ci = ci
            ).onSuccess {
                // 회원가입 성공
                _uiState.update { it.copy(isLoading = false, isRegistrationSuccess = true) }
            }.onFailure { error ->
                // 회원가입 실패
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun errorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}