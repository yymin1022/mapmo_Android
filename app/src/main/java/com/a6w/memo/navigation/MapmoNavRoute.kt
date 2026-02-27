package com.a6w.memo.navigation

import kotlinx.serialization.Serializable

/**
 * Mapmo Nav Routes
 * - Defines routes for each screen
 * - [MapmoNavHost] references these routes
 * - Each route must be [Serializable]
 */
@Serializable
sealed interface MapmoNavRoute {
    // Home
    @Serializable
    data object Home: MapmoNavRoute

    // Mapmo
    @Serializable
    data class Mapmo(
        // Mapmo ID. If null, unavailable mapmo
        val mapmoID: String? = null,
    ): MapmoNavRoute

    // Setting
    @Serializable
    data object Setting: MapmoNavRoute
}