package com.a6w.memo.common.model

/**
 * Kakao Map Camera Focus
 * - Represents a camera focus position for Kakao Map View
 * @param latitude Latitude of marker
 * @param longitude Longitude of marker
 */
data class KakaoMapCameraFocus(
    val latitude: Float,
    val longitude: Float,
)