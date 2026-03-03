package com.a6w.memo.route.mapmo.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.a6w.memo.common.model.MapCameraFocusData
import com.a6w.memo.common.model.MapMarkerData
import com.a6w.memo.data.repository.MapmoRepositoryImpl
import com.a6w.memo.domain.model.Mapmo
import com.a6w.memo.navigation.MapmoNavRoute
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Mapmo ViewModel Class
 * - Manage states of Mapmo Screen
 */
class MapmoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // TODO: Mapmo ViewModel Instance Setup
    private val repository = MapmoRepositoryImpl()

    private val _uiState = MutableStateFlow(MapmoUiState())
    val uiState: StateFlow<MapmoUiState> = _uiState
    // 편집 모드 상태
    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing

    // 편집 중인 내용
    private val _editingContent = MutableStateFlow("")
    val editingContent: StateFlow<String> = _editingContent

    private val route = savedStateHandle.toRoute<MapmoNavRoute.Mapmo>()
    var mapmoID = ""
    init {
        mapmoID = route.mapmoID?:""
    }

    fun loadMapmo( userID: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null
            )
            val result = repository.getMapmo(mapmoID, userID)

            // 결과에 따라 상태 업데이트
            _uiState.value = if (result != null) {
                // 지도 데이터 생성
                val cameraFocus = createCameraFocus(result)
                val markers = createMarkers(result)

                MapmoUiState(
                    mapmo = result,
                    isLoading = false,
                    errorMessage = null,
                    mapCameraFocus = cameraFocus,
                    mapMarkerList = markers
                )
            } else {
                MapmoUiState(
                    mapmo = null,
                    isLoading = false,
                    errorMessage = "Mapmo를 찾을 수 없습니다",
                    mapCameraFocus = null,
                    mapMarkerList = null
                )
            }
        }
    }
    // 편집 모드 토글
    fun toggleEditMode() {
        _isEditing.value = !_isEditing.value
        val currentMapmo = _uiState.value.mapmo
        if (_isEditing.value) {
            // 편집 종료 시 원래 내용으로 되돌림
            _editingContent.value = currentMapmo?.content.orEmpty()
        }

    }

    // 내용 변경
    fun updateEditingContent(newContent: String) {
        _editingContent.value = newContent
    }

    // 저장
    fun saveContent(userID: String) {
        viewModelScope.launch {
            val currentMapmo = _uiState.value.mapmo ?: return@launch

            val updatedMapmo = currentMapmo.copy(
                content = _editingContent.value
            )

            // UI 즉시 업데이트
            _uiState.value = _uiState.value.copy(
                mapmo = updatedMapmo
            )
            _isEditing.value = false

            // 서버 업데이트
            val success = repository.updateMapmo(
                mapmoContent = updatedMapmo,
                userID = userID
            )

            if (!success) {
                // 실패 시 롤백
                _uiState.value = _uiState.value.copy(
                    mapmo = currentMapmo,
                    errorMessage = "내용 저장 실패"
                )
                _editingContent.value = currentMapmo.content
                println("❌ 내용 저장 실패")
            } else {
                println("✅ 내용 저장 성공")
            }
        }
    }
    // 알림 상태 토글 함수 추가
    fun toggleNotification(userID: String) {
        viewModelScope.launch {
            val currentMapmo = _uiState.value.mapmo ?: return@launch

            // 알림 상태 반전
            val updatedMapmo = currentMapmo.copy(
                isNotifyEnabled = !currentMapmo.isNotifyEnabled
            )
            _uiState.value = _uiState.value.copy(
                mapmo = updatedMapmo
            )
            // 2. 그 다음 서버 업데이트
            val success = repository.updateMapmo(
                mapmoContent = updatedMapmo,
                userID = userID
            )

            // 3. 실패하면 원래 상태로 되돌리기 (롤백)
            if (!success) {
                _uiState.value = _uiState.value.copy(
                    mapmo = currentMapmo,  // 원래 상태로 복구
                    errorMessage = "알림 상태 업데이트 실패"
                )
                println("❌ 알림 상태 업데이트 실패 - 롤백")
            } else {
                println("✅ 알림 상태 업데이트 성공: ${updatedMapmo.isNotifyEnabled}")
            }
        }
    }
    // Mapmo에서 카메라 포커스 생성
    private fun createCameraFocus(mapmo: Mapmo): MapCameraFocusData {
        return MapCameraFocusData(
            latitude = mapmo.location.lat.toFloat(),   // Double → Float 변환
            longitude = mapmo.location.lng.toFloat(),   // Double → Float 변환
        )
    }

    // Mapmo에서 마커 생성
    private fun createMarkers(mapmo: Mapmo): List<MapMarkerData> {
        return listOf(
            MapMarkerData(
                latitude = mapmo.location.lat.toFloat(),    // Double → Float 변환
                longitude = mapmo.location.lng.toFloat(),   // Double → Float 변환
                markerTitle = mapmo.content,                // label → markerTitle
                onClick = null                              // 클릭 이벤트 없음
            )
        )
    }
}