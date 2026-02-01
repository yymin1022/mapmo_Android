package com.a6w.memo.domain.model

/**
 * MapmoListItem model
 * - List of memos for each label
 * - Label & Mapmo Info
 */
data class MapmoListItem(
    // Label info
    val labelItem: Label,
    // MapmoList
    val mapmoList: List<Mapmo>,
)
