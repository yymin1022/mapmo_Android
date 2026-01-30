package com.a6w.memo.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.a6w.memo.route.home.HomeScreen

/**
 * Mapmo Nav Host
 * - Manage navigation for each screen
 * - Each routes are defined at [MapmoNavRoute] as data class
 */
@Composable
fun MapmoNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = MapmoNavRoute.Home,
    ) {
        // Home
        composable<MapmoNavRoute.Home> {
            HomeScreenNav(
                modifier = Modifier,
            )
        }
    }
}

// Home Screen Nav
@Composable
private fun HomeScreenNav(
    modifier: Modifier = Modifier,
) {
    // Home Screen
    HomeScreen(
        modifier = modifier
            .fillMaxSize(),
    )
}