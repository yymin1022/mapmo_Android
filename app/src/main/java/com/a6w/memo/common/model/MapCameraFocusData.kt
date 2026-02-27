package com.a6w.memo.common.model

/**
 * Map Camera Focus
 * - Represents a camera focus position for Kakao Map View
 * - It can be re-used for any maps
 * @param latitude Latitude of marker
 * @param longitude Longitude of marker
 */
data class MapCameraFocusData(
    val latitude: Float,
    val longitude: Float,
)