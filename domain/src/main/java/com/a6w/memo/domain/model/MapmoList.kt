package com.a6w.memo.domain.model

/**
 * MapmoList model
 * - Full list of Mapmos
 */
data class MapmoList(
    // Number of Mapmo
    val count: Int,
    // List of Mapmo of each Label
    val list: List<MapmoListItem>,
)

