package com.a6w.memo.domain.repository

import com.a6w.memo.domain.model.MapmoList

/**
 * MapmoListRepository interface
 */
interface MapmoListRepository {
    // Fetch Mapmo information grouped by label
    suspend fun getMapmoList(userID: String): MapmoList?
    // Remove MapmoList cache
    suspend fun removeCachedMapmoList(userID: String): Boolean
}