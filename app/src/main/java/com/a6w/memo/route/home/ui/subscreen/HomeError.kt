package com.a6w.memo.route.home.ui.subscreen

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.a6w.memo.route.home.viewmodel.HomeUiState

/**
 * Home Error UI
 */
@Composable
fun HomeError(
    modifier: Modifier = Modifier,
    uiState: HomeUiState.Error,
) {
    val errMessage = uiState.errMessage

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = errMessage ?: "",
            fontSize = 16.sp,
        )
    }
}