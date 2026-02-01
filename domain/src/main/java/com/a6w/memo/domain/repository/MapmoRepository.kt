package com.a6w.memo.domain.repository

import com.a6w.memo.domain.model.Mapmo


/**
 * MapmoRepository interface
 */
interface MapmoRepository {
    // Add a new Mapmo
    suspend fun addMapmo(mapmoContent : Mapmo, labelId:String, userId: String): Boolean
    // Update Mapmo information
    suspend fun updateMapmo(mapmoContent: Mapmo, labelId:String, userId: String): Boolean
    // Retrieve Mapmo detailed information
    suspend fun getMapmo(mapmoId : String, userId:String): Mapmo?
}