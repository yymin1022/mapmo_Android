package com.a6w.memo.route.mapmo.ui

import android.icu.text.SimpleDateFormat
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.a6w.memo.route.mapmo.viewmodel.MapmoViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.a6w.memo.common.model.MapCameraFocusData
import com.a6w.memo.common.model.MapMarkerData
import com.a6w.memo.common.ui.KakaoMapView
import com.a6w.memo.domain.model.Mapmo
import com.a6w.memo.navigation.MapmoNavRoute

import java.util.Date
import java.util.Locale

private val BOTTOM_SHEET_HEIGHT_MINIMUM_DP = 180.dp
private val BOTTOM_SHEET_RADIUS_DP = 16.dp

/**
 * Mapmo Screen
 * - Mapmo UI for mapmo application
 * - Uses state from [com.a6w.memo.route.mapmo.MapmoViewModel]
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapmoScreen(
    modifier: Modifier = Modifier,
    mapmoID: String,
    userID: String = "test_user_1",  // 테스트용
    viewModel: MapmoViewModel = hiltViewModel(),
    navigationPop: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isEditing by viewModel.isEditing.collectAsStateWithLifecycle()
    val editingContent by viewModel.editingContent.collectAsStateWithLifecycle()

    // TODO: Mapmo UI Implementation
    // 화면 진입 시 데이터 로드
    LaunchedEffect(mapmoID) {
        viewModel.loadMapmo( userID)
    }

    Column(modifier = modifier.fillMaxSize()) {
        // 커스텀 TopBar (고정)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = navigationPop) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                    Text(
                        text = "MAPMO",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                // 편집/저장 버튼
                if (isEditing) {
                    TextButton(onClick = { viewModel.saveContent(userID) }) {
                        Text("저장", fontWeight = FontWeight.Bold)
                    }
                } else {
                    IconButton(onClick = { viewModel.toggleEditMode() }) {
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "편집"
                        )
                    }
                }


            }

        }
// 로딩 상태
        if (uiState.isLoading) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(400.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

        }

        // 에러 메시지
        uiState.errorMessage?.let { error ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Red.copy(alpha = 0.1f)
                )
            ) {
                Text(
                    text = "❌ $error",
                    modifier = Modifier.padding(16.dp),
                    color = Color.Red
                )
            }
        }

        // Mapmo 데이터
        uiState.mapmo?.let { mapmo ->
            // 스크롤 가능한 컨텐츠
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.4f)
            ) {

                // 상단 내용 섹션
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {


                        // 내용 박스
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            val contentText = if (isEditing) editingContent else mapmo.content
                            if (isEditing) {
                                // 편집 모드 - TextField
                                TextField(
                                    value = contentText,
                                    onValueChange = { viewModel.updateEditingContent(it) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    textStyle = LocalTextStyle.current.copy(
                                        fontSize = 18.sp,
                                        lineHeight = 28.sp
                                    ),
                                    colors = TextFieldDefaults.colors(
                                        focusedContainerColor = Color.Transparent,
                                        unfocusedContainerColor = Color.Transparent,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent
                                    ),
                                    placeholder = { Text("내용을 입력하세요") }
                                )
                            } else {
                                Text(
                                    text = contentText,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Medium,
                                    lineHeight = 28.sp,
                                    modifier = Modifier.padding(20.dp)
                                )
                            }
                        }
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // 업데이트 일시
                            Text(
                                text = SimpleDateFormat("yyyy년 MM월 dd일 HH:mm", Locale.KOREAN)
                                    .format(Date(mapmo.updatedAt * 1000)),
                                fontSize = 14.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                            NotificationRow(
                                label = "알림",
                                isEnabled = mapmo.isNotifyEnabled,
                                onToggle = {
                                    // TODO: ViewModel에서 알림 상태 업데이트
                                    viewModel.toggleNotification(userID)
                                }
                            )
                        }

                    }
                }

                // 구분선
                item {
                    HorizontalDivider()
                }

                // 지도 섹션
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(500.dp)  // 고정 높이
                    ) {
                        MapmoMapView(
                            modifier = Modifier.fillMaxSize(),
                            mapCameraFocus = uiState.mapCameraFocus,
                            mapMarkerList = uiState.mapMarkerList
                        )
                    }
                }
            }
        }
    }
}

/**
 * Map View for Mapmo
 */
@Composable
private fun MapmoMapView(
    modifier: Modifier = Modifier,
    mapCameraFocus: MapCameraFocusData?,
    mapMarkerList: List<MapMarkerData>?,
) {
    Box(modifier = modifier) {
        if (mapCameraFocus != null && mapMarkerList != null) {
            KakaoMapView(
                modifier = Modifier.fillMaxSize(),
                cameraFocus = mapCameraFocus,
                markers = mapMarkerList
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("지도를 불러올 수 없습니다", color = Color.Gray)
            }
        }
    }
}

@Composable
private fun NotificationRow(
    label: String,
    isEnabled: Boolean,
    onToggle: () -> Unit,  // 콜백 추가
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onToggle() },
    ) {
        // Checkbox
        Checkbox(
            checked = isEnabled,
            onCheckedChange = { onToggle() }
        )
    }
}

