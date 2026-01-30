package com.a6w.memo.route.setting

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

/**
 * Setting Screen
 * - Setting UI for mapmo application
 * - Uses state from [SettingViewModel]
 */
@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingViewModel = hiltViewModel(),
    navigationPop: () -> Unit,
) {
    // TODO: Setting UI Implementation
    Column(
        modifier = modifier,
    ) {
        Text("Setting Screen")

        Button(
            onClick = navigationPop,
        ) {
            Text("Back")
        }
    }
}