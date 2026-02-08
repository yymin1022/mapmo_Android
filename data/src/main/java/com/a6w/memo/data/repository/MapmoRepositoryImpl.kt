package com.a6w.memo.data.repository

import com.a6w.memo.domain.model.Location
import com.a6w.memo.domain.model.Mapmo
import com.a6w.memo.domain.repository.MapmoRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await

class MapmoRepositoryImpl : MapmoRepository {
    val db = FirebaseFirestore.getInstance()

    override suspend fun getMapmo(mapmoID: String, userID: String): Mapmo? {
        try {
            // Fetch mapmo by ID
            val document = db.collection("mapmo").document(mapmoID).get().await()
            // Check if the document exists
            if (document.exists()) {
                // Extract GeoPoint from Firestore
                val geoPoint = document.getGeoPoint("location") ?: return null

                // Convert GeoPoint to Location model
                val location = Location(
                    lat = geoPoint.latitude,
                    lng = geoPoint.longitude,
                )

                // Extract updatedAt timestamp
                val updatedAt: Timestamp? = document.get("updatedAt") as? Timestamp
                // Convert Firestore document to Mapmo object
                return Mapmo(
                    mapmoID = document.id,
                    content = document.getString("content") ?: "",
                    isNotifyEnabled = document.getBoolean("isNotifyEnabled") ?: false,
                    labelID = document.getString("labelID") ?: "",
                    location = location,
                    updatedAt = updatedAt?.seconds ?: 0,
                )
            } else {
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    override suspend fun addMapmo(mapmoContent: Mapmo, userID: String): Boolean {
        try {
            // Convert Location to Firestore GeoPoint
            val location = GeoPoint(mapmoContent.location.lat, mapmoContent.location.lng)
            val updatedAt = Timestamp.now()

            // Create map data to upload to Firestore
            val mapmoData = hashMapOf(
                "content" to mapmoContent.content,
                "isNotifyEnabled" to mapmoContent.isNotifyEnabled,
                "labelID" to mapmoContent.labelID,
                "location" to location,
                "updatedAt" to updatedAt,
                "userID" to userID,
            )

            // Add a new document to the mapmo collection
            db.collection("mapmo")
                .add(mapmoData)
                .await()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    override suspend fun updateMapmo(
        mapmoContent: Mapmo, userID: String
    ): Boolean {
        try {
            // Convert Location to Firestore GeoPoint
            val location = GeoPoint(mapmoContent.location.lat, mapmoContent.location.lng)
            // Set current timestamp for update
            val updatedAt = Timestamp.now()
            // Create updated data mapmo
            val mapmoData = mapOf(
                "content" to mapmoContent.content,
                "isNotifyEnabled" to mapmoContent.isNotifyEnabled,
                "labelID" to mapmoContent.labelID,
                "location" to location,
                "updatedAt" to updatedAt,
            )

            // Update the existing mapmo document
            db.collection("mapmo")
                .document(mapmoContent.mapmoID)
                .update(mapmoData)
                .await()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

}