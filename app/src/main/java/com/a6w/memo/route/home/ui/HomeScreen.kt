package com.a6w.memo.route.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.a6w.memo.common.model.MapCameraFocusData
import com.a6w.memo.common.model.MapMarkerData
import com.a6w.memo.common.ui.KakaoMapView
import com.a6w.memo.route.home.viewmodel.HomeViewModel
import androidx.compose.runtime.collectAsState

/**
 * Home Screen
 * - Home UI for mapmo application
 * - Uses state from [HomeViewModel]
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToMapmo: () -> Unit,
    navigateToSetting: () -> Unit,
) {
    // UI States
    val uiState = viewModel.uiState.collectAsState()
    val mapmoList = uiState.value.mapmoList
    val mapCameraFocus = uiState.value.mapCameraFocus
    val mapMarkerList = uiState.value.mapMarkerList

    // Map View for Mapmo
    MapmoMapView(
        modifier = modifier,
        mapCameraFocus = mapCameraFocus,
        mapMarkerList = mapMarkerList
    )

    // TODO: Remove Debug UI
    DebugUI(
        navigateToMapmo = navigateToMapmo,
        navigateToSetting = navigateToSetting,
    )
}

/**
 * Map View for Mapmo
 */
@Composable
private fun MapmoMapView(
    modifier: Modifier = Modifier,
    mapCameraFocus: MapCameraFocusData? = null,
    mapMarkerList: List<MapMarkerData>? = null,
) {
    Box(
        modifier = modifier,
    ) {
        KakaoMapView(
            modifier = Modifier
                .fillMaxSize(),
            cameraFocus = mapCameraFocus,
            markers = mapMarkerList,
        )
    }
}

/**
 * TODO: Remove Debug UI
 */
@Composable
private fun DebugUI(
    modifier: Modifier = Modifier,
    navigateToMapmo: () -> Unit,
    navigateToSetting: () -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        Button(
            onClick = navigateToMapmo,
        ) {
            Text("[DEBUG] Open Mapmo Screen")
        }

        Button(
            onClick = navigateToSetting,
        ) {
            Text("[DEBUG] Open Setting Screen")
        }
    }
}