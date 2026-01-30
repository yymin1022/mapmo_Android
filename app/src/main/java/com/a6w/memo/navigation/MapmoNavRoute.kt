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
    // TODO: Define nav routes for each screen
    //       DummyRoute is dummy as its name represents
    @Serializable
    data object DummyRoute: MapmoNavRoute
}