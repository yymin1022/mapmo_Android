package com.a6w.memo.domain.service

/**
 * Mapmo Notification Service
 * - Defines notification method
 */
interface MapmoNotificationService {
    // Show notification
    fun showNotification(title: String, content: String)
}