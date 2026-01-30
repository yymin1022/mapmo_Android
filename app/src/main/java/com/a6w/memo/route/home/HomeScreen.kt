package com.a6w.memo.route.home

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

/**
 * Home Screen
 * - Home UI for mapmo application
 * - Uses state from [HomeViewModel]
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    // TODO: Home UI Implementation
    Box(
        modifier = modifier,
    ) {
        Text("Home Screen")
    }
}