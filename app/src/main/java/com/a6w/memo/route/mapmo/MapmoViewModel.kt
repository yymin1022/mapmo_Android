package com.a6w.memo.route.mapmo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.navigation.toRoute
import com.a6w.memo.navigation.MapmoNavRoute
import javax.inject.Inject

/**
 * Mapmo ViewModel Class
 * - Manage states of Mapmo Screen
 */
class MapmoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): ViewModel() {
    // TODO: Mapmo ViewModel Instance Setup

    // TODO: Temporary code for mapmo ID Debugging
    private val route = savedStateHandle.toRoute<MapmoNavRoute.Mapmo>()
    init {
        val mapmoID = route.mapmoID
        println(mapmoID)
    }
}