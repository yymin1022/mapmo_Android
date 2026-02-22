package com.a6w.memo.route.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.a6w.memo.data.repository.MapmoListRepositoryImpl
import com.a6w.memo.domain.repository.MapmoListRepository
import com.a6w.memo.domain.repository.MapmoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Home ViewModel Class
 * - Manage states of Home Screen
 */
class HomeViewModel
@Inject constructor(
    private val mapmoListRepository: MapmoListRepository,
    private val mapmoRepository: MapmoRepository
): ViewModel() {
    companion object {
        // TODO: User ID must be managed with User Info
        private const val TEST_USER_ID = "test_user_1"
    }

    // UI State variable
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * Get mapmo list instance and save as ui state
     */
    fun getMapmoList() {
        viewModelScope.launch {
            // Get mapmo list from repository
            val mapmoList = mapmoListRepository.getMapmoList(TEST_USER_ID)

            // Update as UI State
            _uiState.value = _uiState.value.copy(
                mapmoList = mapmoList,
            )
        }
    }
}