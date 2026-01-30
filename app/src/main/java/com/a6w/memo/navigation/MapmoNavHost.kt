package com.a6w.memo.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

/**
 * Mapmo Nav Host
 * - Manage navigation for each screen
 * - Each routes are defined at [MapmoNavRoute] as data class
 * - Routes each screen by [MapmoNavRoute.routeName]
 */
@Composable
fun MapmoNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MapmoNavRoute.DummyRoute,
    ) {
        // TODO: Add Entrypoint for each route
        composable<MapmoNavRoute.DummyRoute> {
            // TODO: Screen for each route
        }
    }
}