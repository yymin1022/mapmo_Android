package com.a6w.memo.domain.model
/**
 * Mapmo model
 * - Detailed information of Mapmo
 * - Available on the memo detail page
 */
data class Mapmo(
    // Mapmo unique Id
    val mapmoId : String,
    // Mapmo title & content
    val content: String,
    // Mapmo notification settings status
    val isNotifyEnabled : Boolean,
    // Label name of the Mapmo
    val labelName: String,
    // Label icon color of the Mapmo
    val labelColor: String,
    // Location of Mapmo
    val location : Location,
    // Last modified date and time
    val updatedAt : Long,
)



