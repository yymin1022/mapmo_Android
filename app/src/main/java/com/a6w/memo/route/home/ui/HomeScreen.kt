package com.a6w.memo.route.home.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.a6w.memo.common.ui.KakaoMapView
import com.a6w.memo.route.home.viewmodel.HomeViewModel

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
    // TODO: Home UI Implementation
    Column(
        modifier = modifier,
    ) {
        Text("Home Screen")

        Button(
            onClick = navigateToMapmo,
        ) {
            Text("Open Mapmo Screen")
        }

        Button(
            onClick = navigateToSetting,
        ) {
            Text("Open Setting Screen")
        }

        KakaoMapView(
            modifier = Modifier
                .fillMaxSize(),
        )
    }
}