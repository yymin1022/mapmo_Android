package com.a6w.memo.domain.model

/**
 * Mapmo model
 * - Detailed information of Mapmo
 * - Available on the memo detail page
 */
data class Mapmo(
    // Mapmo unique Id
    val mapmoID: String,
    // Mapmo title & content
    val content: String,
    // Mapmo notification settings status
    val isNotifyEnabled: Boolean,
    // Label name of the Mapmo
    val labelID: String? = null,
    // Location of Mapmo
    val location: Location,
    // Last modified date and time
    val updatedAt: Long,
)



