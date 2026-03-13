package com.a6w.memo.data.repository

import com.a6w.memo.domain.model.Location
import com.a6w.memo.domain.repository.GeofenceRepository

class GeofenceRepositoryImpl: GeofenceRepository {
    override fun registerGeofence(
        mapmoID: String,
        location: Location,
    ): Result<Unit> {
        TODO("Not yet implemented")
    }

    override fun unregisterGeofence(
        mapmoID: String,
    ): Result<Unit> {
        TODO("Not yet implemented")
    }
}