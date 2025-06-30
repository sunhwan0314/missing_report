package com.example.awstestapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.awstestapp.data.remote.dto.SightingDto
import com.example.awstestapp.domain.repository.MapRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// 지도 화면의 UI 상태
data class MapUiState(
    val isLoading: Boolean = true,
    val sightings: List<SightingDto> = emptyList(),
    val errorMessage: String? = null
)

class MapViewModel(
    private val mapRepository: MapRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MapUiState())
    val uiState = _uiState.asStateFlow()

    init {
        // ViewModel이 생성되자마자 모든 목격담 데이터를 불러옵니다.
        fetchAllSightings()
    }

    private fun fetchAllSightings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            mapRepository.getAllSightings()
                .onSuccess { sightingList ->
                    _uiState.update {
                        it.copy(isLoading = false, sightings = sightingList)
                    }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }
}