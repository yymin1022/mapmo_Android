package com.a6w.memo.data.repository

import com.a6w.memo.data.firebase.FirestoreKey
import com.a6w.memo.domain.model.Location
import com.a6w.memo.domain.model.Mapmo
import com.a6w.memo.domain.repository.MapmoRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await

/**
 * MapmoRepositoryImpl
 *
 * - Repository implementation for managing Mapmo data in Firestore
 * - Provides operations for retrieving, creating, and updating Mapmo documents
 *
 * Responsibilities:
 * - Fetch a single Mapmo by ID
 * - Add a new Mapmo
 * - Update an existing Mapmo
 *
 */
class MapmoRepositoryImpl: MapmoRepository {
    private val firestoreDB = FirebaseFirestore.getInstance()
    private val mapmoCollection = firestoreDB.collection(FirestoreKey.COLLECTION_KEY_MAPMO)
    override suspend fun getMapmo(
        mapmoID: String,
        userID: String,
    ): Mapmo? {
        try {
            // Fetch mapmo by ID
            val document =
                mapmoCollection.document(mapmoID).get()
                    .await()
            // Check if the document exists

            if (document.exists().not()) return null
            // Extract GeoPoint from Firestore
            val geoPoint = document.getGeoPoint(FirestoreKey.DOCUMENT_KEY_LOCATION) ?: return null

            // Location Lat/Lng Info
            val mapmoLat = geoPoint.latitude
            val mapmoLng = geoPoint.longitude

            // Location Data
            val location = Location(
                lat = mapmoLat,
                lng = mapmoLng,
            )

            val mapmoID = document.id
            val content = document.getString(FirestoreKey.DOCUMENT_KEY_CONTENT) ?: ""
            val isNotifyEnabled =
                document.getBoolean(FirestoreKey.DOCUMENT_KEY_IS_NOTIFY_ENABLED) ?: false
            val labelID = document.getString(FirestoreKey.DOCUMENT_KEY_LABEL_ID) // nullable
            val timeStampUpdatedAt =
                document.get(FirestoreKey.DOCUMENT_KEY_UPDATED_AT) as? Timestamp
            val updatedAt = timeStampUpdatedAt?.seconds ?: 0
            // Mapmo Data
            return Mapmo(
                mapmoID = mapmoID,
                content = content,
                isNotifyEnabled = isNotifyEnabled,
                labelID = labelID,
                location = location,
                updatedAt = updatedAt,
            )

        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

    }

    override suspend fun addMapmo(
        mapmoContent: Mapmo,
        userID: String,
    ): Boolean {
        try {
            // Convert Location to Firestore GeoPoint
            val location = GeoPoint(mapmoContent.location.lat, mapmoContent.location.lng)
            val updatedAt = Timestamp.now()

            // Create map data to upload to Firestore
            val mapmoData = hashMapOf(
                FirestoreKey.DOCUMENT_KEY_CONTENT to mapmoContent.content,
                FirestoreKey.DOCUMENT_KEY_IS_NOTIFY_ENABLED to mapmoContent.isNotifyEnabled,
                FirestoreKey.DOCUMENT_KEY_LABEL_ID to mapmoContent.labelID,
                FirestoreKey.DOCUMENT_KEY_LOCATION to location,
                FirestoreKey.DOCUMENT_KEY_UPDATED_AT to updatedAt,
                FirestoreKey.DOCUMENT_KEY_USER_ID to userID,
            )

            // Add a new document to the mapmo collection
            mapmoCollection
                .add(mapmoData)
                .await()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

    }

    override suspend fun updateMapmo(
        mapmoContent: Mapmo,
        userID: String,
    ): Boolean {
        try {
            // Convert Location to Firestore GeoPoint
            val location = GeoPoint(mapmoContent.location.lat, mapmoContent.location.lng)
            // Set current timestamp for update
            val updatedAt = Timestamp.now()
            // Create updated data mapmo
            val mapmoData = mapOf(
                FirestoreKey.DOCUMENT_KEY_CONTENT to mapmoContent.content,
                FirestoreKey.DOCUMENT_KEY_IS_NOTIFY_ENABLED to mapmoContent.isNotifyEnabled,
                FirestoreKey.DOCUMENT_KEY_LABEL_ID to mapmoContent.labelID,
                FirestoreKey.DOCUMENT_KEY_LOCATION to location,
                FirestoreKey.DOCUMENT_KEY_UPDATED_AT to updatedAt,
            )

            // Update the existing mapmo document
            mapmoCollection
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