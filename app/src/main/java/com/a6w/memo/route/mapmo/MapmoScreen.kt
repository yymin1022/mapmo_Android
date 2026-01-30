package com.a6w.memo.route.mapmo

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

/**
 * Mapmo Screen
 * - Mapmo UI for mapmo application
 * - Uses state from [MapmoViewModel]
 */
@Composable
fun MapmoScreen(
    modifier: Modifier = Modifier,
    viewModel: MapmoViewModel = hiltViewModel(),
    navigationPop: () -> Unit,
) {
    // TODO: Mapmo UI Implementation
    Column(
        modifier = modifier,
    ) {
        Text("Mapmo Screen")

        Button(
            onClick = navigationPop,
        ) {
            Text("Back")
        }
    }
}