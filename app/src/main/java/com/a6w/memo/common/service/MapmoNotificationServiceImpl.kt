package com.a6w.memo.common.service

import android.content.Context
import com.a6w.memo.domain.service.MapmoNotificationService
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Mapmo Notification Service Implementation
 * - Implements [MapmoNotificationService]
 */
class MapmoNotificationServiceImpl @Inject constructor(
    @ApplicationContext
    private val context: Context
): MapmoNotificationService {
    override fun showNotification(title: String, content: String) {
        // TODO: Implement Notification Logic
    }
}