package com.a6w.memo.route.mapmo.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.a6w.memo.common.model.MapCameraFocusData
import com.a6w.memo.common.model.MapMarkerData
import com.a6w.memo.domain.model.Label
import com.a6w.memo.domain.model.Mapmo
import com.a6w.memo.domain.repository.LabelRepository
import com.a6w.memo.domain.repository.MapmoRepository
import com.a6w.memo.navigation.MapmoNavRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Mapmo detail screen.
 * Manages UI state including memo content, edit mode, label selection, and map data.
 */
@HiltViewModel
class MapmoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val mapmoRepository: MapmoRepository,
    private val labelRepository: LabelRepository,
): ViewModel() {

    companion object {
        // TODO: User ID must be managed with User Info
        private const val TEST_USER_ID = "test_user_1"
    }

    private val route = savedStateHandle.toRoute<MapmoNavRoute.Mapmo>()
    private val mapmoID: String = route.mapmoID ?: ""

    private val _uiState = MutableStateFlow(MapmoUiState())
    val uiState: StateFlow<MapmoUiState> = _uiState.asStateFlow()

    private val _editingContent = MutableStateFlow("")
    val editingContent: StateFlow<String> = _editingContent.asStateFlow()

    private val _labelList = MutableStateFlow<List<Label>>(emptyList())
    val labelList: StateFlow<List<Label>> = _labelList.asStateFlow()

    /**
     * Fetch Mapmo data and associated label for the current [mapmoID].
     * On success, also prepares map camera focus and marker data.
     */
    fun loadMapmo() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                )
            }

            val result = mapmoRepository.getMapmo(
                mapmoID = mapmoID,
                userID = TEST_USER_ID,
            )

            // Mapmo is unavailable
            if (result == null) {
                _uiState.value = MapmoUiState(
                    isLoading = false,
                    errorMessage = "Mapmo를 찾을 수 없습니다",
                )
                return@launch
            }

            val label = runCatching {
                labelRepository.getLabel(
                    labelID = result.labelID ?: "",
                    userID = TEST_USER_ID,
                )
            }.getOrNull()

            // Label is unavailable
            if (label == null) {
                _uiState.value = MapmoUiState(
                    isLoading = false,
                    errorMessage = "label을 찾을 수 없습니다",
                )
                return@launch
            }
            val labelLat = label.location.lat
            val labelLng = label.location.lng
            val mapmoContent = result.content

            val cameraFocus = createCameraFocus(labelLat, labelLng)
            val markers = createMarkers(labelLat, labelLng, mapmoContent)

            _uiState.value = MapmoUiState(
                mapmo = result,
                label = label,
                isLoading = false,
                errorMessage = null,
                mapCameraFocus = cameraFocus,
                mapMarkerList = markers,
            )
        }
    }

    /**
     * Toggle between view and edit mode.
     * Copies current content into the editing buffer when entering edit mode.
     */
    fun toggleEditMode() {
        val enteringEditMode = !_uiState.value.isEditing
        val content = _uiState.value.mapmo?.content.orEmpty()
        _uiState.update {
            it.copy(
                isEditing = enteringEditMode,
            )
        }
        if (enteringEditMode) {
            _editingContent.value = content
        }
    }

    /**
     * Update the in-progress editing content.
     *
     * @param newContent The latest text entered by the user.
     */
    fun updateEditingContent(newContent: String) {
        _editingContent.value = newContent
    }

    /**
     * Persist the edited content to the repository.
     * Applies an optimistic UI update and rolls back on failure.
     */
    fun saveContent() {
        viewModelScope.launch {
            val currentMapmo = _uiState.value.mapmo ?: return@launch

            val updatedMapmo = currentMapmo.copy(
                content = _editingContent.value,
            )

            // Optimistic update: reflect changes in UI before server confirms
            _uiState.update {
                it.copy(
                    mapmo = updatedMapmo,
                    isEditing = false,
                )
            }

            val success = mapmoRepository.updateMapmo(
                mapmoContent = updatedMapmo,
                userID = TEST_USER_ID,
            )

            if (!success) {
                // Rollback to previous state on failure
                _uiState.update {
                    it.copy(
                        mapmo = currentMapmo,
                        errorMessage = "내용 저장 실패",
                    )
                }
                _editingContent.value = currentMapmo.content
            }
        }
    }

    /**
     * Toggle the notification enabled state for the current Mapmo.
     * Applies an optimistic UI update and rolls back on failure.
     */
    fun toggleNotification() {
        viewModelScope.launch {
            val currentMapmo = _uiState.value.mapmo ?: return@launch

            val updatedMapmo = currentMapmo.copy(
                isNotifyEnabled = !currentMapmo.isNotifyEnabled,
            )

            // Optimistic update
            _uiState.update {
                it.copy(
                    mapmo = updatedMapmo,
                )
            }
            val success = mapmoRepository.updateMapmo(
                mapmoContent = updatedMapmo,
                userID = TEST_USER_ID,
            )

            if (!success) {
                // Rollback to previous notification state on failure
                _uiState.update {
                    it.copy(
                        mapmo = currentMapmo,
                        errorMessage = "알림 상태 업데이트 실패",
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        errorMessage = null,
                    )
                }
            }
        }
    }

    /**
     * Fetch all labels available for the current user.
     * Opens the label selector on success, shows an error on failure.
     */
    fun loadLabelList() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLabelListLoading = true,
                )
            }
            val result = runCatching {
                labelRepository.getLabelList(userID = TEST_USER_ID)
            }.getOrNull()

            _uiState.update {
                it.copy(
                    isLabelListLoading = false,
                )
            }
            if (result == null) {
                _uiState.update {
                    it.copy(
                        errorMessage = "라벨 목록을 불러올 수 없습니다",
                    )
                }
                return@launch
            }

            _labelList.value = result.list
            _uiState.update {
                it.copy(
                    isLabelSelectorOpen = true,
                )
            }
        }
    }

    /**
     * Replace the current label with the selected one.
     * Closes the label selector after selection.
     *
     * @param label The [Label] selected by the user.
     */
    fun selectLabel(label: Label) {
        _uiState.update {
            it.copy(
                label = label,
                isLabelListLoading = false,
            )
        }
    }

    /**
     * Close the label selector without changing the current label.
     */
    fun closeLabelSelector() {
        _uiState.update {
            it.copy(
                isLabelSelectorOpen = false,
            )
        }
    }

    /**
     * Create a [MapCameraFocusData] centered on the Mapmo's location.
     *
     * @param lat latitude of Label Location
     * @param lng longitude of Label Location
     */
    private fun createCameraFocus(lat: Double, lng: Double): MapCameraFocusData {
        val lat = lat.toFloat()  // precision loss: Double → Float intentional
        val lng = lng.toFloat()  // precision loss: Double → Float intentional

        return MapCameraFocusData(
            latitude = lat,
            longitude = lng,
        )
    }

    /**
     * Create a single-item marker list from the Mapmo's location.
     *
     *  @param lat latitude of Label Location
     *  @param lng longitude of Label Location
     *  @param markerTitle title of mapmo
     */
    private fun createMarkers(
        lat: Double,
        lng: Double,
        markerTitle: String,
    ): List<MapMarkerData> {
        val lat = lat.toFloat()  // precision loss: Double → Float intentional
        val lng = lng.toFloat()  // precision loss: Double → Float intentional

        return listOf(
            MapMarkerData(
                latitude = lat,
                longitude = lng,
                markerTitle = markerTitle,
                onClick = null,
            )
        )
    }
}