package com.a6w.memo.route.home.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.a6w.memo.common.model.MapCameraFocusData
import com.a6w.memo.common.model.MapMarkerData
import com.a6w.memo.common.ui.KakaoMapView
import com.a6w.memo.route.home.viewmodel.HomeViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.dp
import com.a6w.memo.domain.model.MapmoList

private val BOTTOM_SHEET_HEIGHT_MINIMUM_DP = 180.dp
private val BOTTOM_SHEET_RADIUS_DP = 16.dp

/**
 * Home Screen
 * - Home UI for mapmo application
 * - Uses state from [HomeViewModel]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToMapmo: () -> Unit,
    navigateToSetting: () -> Unit,
) {
    // Bottom Sheet state
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberStandardBottomSheetState()
    )

    // UI States
    val uiState = viewModel.uiState.collectAsState()
    val mapmoList = uiState.value.mapmoList
    val mapCameraFocus = uiState.value.mapCameraFocus
    val mapMarkerList = uiState.value.mapMarkerList

    BottomSheetScaffold(
        modifier = modifier,
        scaffoldState = scaffoldState,
        sheetDragHandle = { BottomSheetDefaults.DragHandle() },
        sheetPeekHeight = BOTTOM_SHEET_HEIGHT_MINIMUM_DP,
        sheetShape = RoundedCornerShape(BOTTOM_SHEET_RADIUS_DP),
        sheetContent = {
            // Mapmo List
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                MapmoListView(
                    modifier = Modifier
                        .fillMaxSize(),
                    mapmoList = mapmoList,
                )
            }
        },
    ) {
        // Map View for Mapmo
        MapmoMapView(
            modifier = Modifier,
            mapCameraFocus = mapCameraFocus,
            mapMarkerList = mapMarkerList
        )
    }

    // TODO: Remove Debug UI
    DebugUI(
        navigateToMapmo = navigateToMapmo,
        navigateToSetting = navigateToSetting,
    )
}

/**
 * Mapmo List
 */
@Composable
private fun MapmoListView(
    modifier: Modifier = Modifier,
    mapmoList: MapmoList?,
) {
    // TODO: Mapmo List UI
    Text("Mapmo List")
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