package com.a6w.memo.common.model

/**
 * Kakao Map Marker
 * - Represents a marker data for Kakao Map View
 * @param latitude Latitude of marker
 * @param longitude Longitude of marker
 * @param markerTitle Title of marker
 * @param onClick (Optional) Callback function when marker is clicked
 */
data class KakaoMapMarker(
    val latitude: Float,
    val longitude: Float,
    val markerTitle: String,
    val onClick: (() -> Unit)? = null,
)