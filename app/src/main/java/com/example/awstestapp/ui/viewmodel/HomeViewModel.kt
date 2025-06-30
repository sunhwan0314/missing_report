package com.example.awstestapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.awstestapp.data.remote.dto.PostListItemDto
import com.example.awstestapp.domain.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 홈 화면의 UI 상태를 나타내는 데이터 클래스
data class HomeUiState(
    val isLoading: Boolean = true, // 처음에는 로딩 상태로 시작
    val errorMessage: String? = null,
    val missingPersons: List<PostListItemDto> = emptyList(),
    val missingAnimals: List<PostListItemDto> = emptyList()
)

class HomeViewModel(private val homeRepository: HomeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // ViewModel이 생성되자마자 데이터를 불러옵니다.
        fetchHomeData()
    }

    // 홈 화면에 필요한 모든 데이터를 가져오는 함수
    fun fetchHomeData() {
        // 코루틴을 사용해 백그라운드에서 네트워크 요청을 실행합니다.
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // 동시에 두 개의 API를 호출하여 결과를 받아옵니다.
            val personsResult = homeRepository.getLatestMissingPersons(limit = 5)
            val animalsResult = homeRepository.getLatestMissingAnimals(limit = 5)

            // 두 요청이 모두 성공했을 때
            personsResult.onSuccess { persons ->
                animalsResult.onSuccess { animals ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            missingPersons = persons,
                            missingAnimals = animals
                        )
                    }
                }
            }

            // 둘 중 하나라도 실패했을 때 (간단한 에러 처리)
            personsResult.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
            animalsResult.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun errorShown() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}