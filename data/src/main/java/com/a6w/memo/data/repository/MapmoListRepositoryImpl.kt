package com.a6w.memo.data.repository

import com.a6w.memo.data.firebase.FirestoreKey
import com.a6w.memo.domain.model.Label
import com.a6w.memo.domain.model.Location
import com.a6w.memo.domain.model.Mapmo
import com.a6w.memo.domain.model.MapmoList
import com.a6w.memo.domain.model.MapmoListItem
import com.a6w.memo.domain.repository.MapmoListRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * MapmoListRepositoryImpl
 *
 * - Fetches MapmoList data from Firestore
 *
 * Responsibilities:
 * - Fetch all Mapmo documents for a specific user
 * - Fetch all Label documents for the same user
 * - Group Mapmo items by labelID
 * - Build and return a MapmoList domain model
 * - Cache the result to avoid redundant server calls
 */
class MapmoListRepositoryImpl : MapmoListRepository {
    private val firestoreDB = FirebaseFirestore.getInstance()
    private val mapmoCollection = firestoreDB.collection(FirestoreKey.COLLECTION_KEY_MAPMO)
    private val labelCollection = firestoreDB.collection(FirestoreKey.COLLECTION_KEY_LABEL)

    // In-memory cache for MapmoList
    private var mapmoListCache: MapmoList? = null

    override suspend fun getMapmoList(
        userID: String,
    ): MapmoList? {
        try {
            // Fetch all mapmo documents that belong to the given userID
            val snapshot = mapmoCollection
                .whereEqualTo(FirestoreKey.DOCUMENT_KEY_USER_ID, userID)
                .get()
                .await()

            // Convert Firestore documents into a list of Mapmo objects
            val mapmoList = snapshot.documents.mapNotNull { document ->
                // Skip if GeoPoint is missing (invalid Mapmo data)
                val geoPoint = document.getGeoPoint(FirestoreKey.DOCUMENT_KEY_LOCATION)
                    ?: return@mapNotNull null

                // Location Lat/Lng Info
                val mapmoLat = geoPoint.latitude
                val mapmoLng = geoPoint.longitude

                // Location Data
                val location = Location(
                    lat = mapmoLat,
                    lng = mapmoLng,
                )

                // Only create Mapmo if location exists
                val mapmoID = document.id
                val content = document.getString(FirestoreKey.DOCUMENT_KEY_CONTENT) ?: ""
                val isNotifyEnabled =
                    document.getBoolean(FirestoreKey.DOCUMENT_KEY_IS_NOTIFY_ENABLED) ?: false
                val labelID = document.getString(FirestoreKey.DOCUMENT_KEY_LABEL_ID) // nullable
                val updatedAt =
                    document.getTimestamp(FirestoreKey.DOCUMENT_KEY_UPDATED_AT)?.seconds ?: 0
                // Mapmo Data
                Mapmo(
                    mapmoID = mapmoID,
                    content = content,
                    isNotifyEnabled = isNotifyEnabled,
                    labelID = labelID,
                    location = location,
                    updatedAt = updatedAt,
                )
            }

            // Fetch all label documents for the user
            val labelSnapshot = labelCollection
                .whereEqualTo(FirestoreKey.DOCUMENT_KEY_USER_ID, userID)
                .get()
                .await()

            // Convert Firestore documents into Label objects
            val labels = labelSnapshot.documents.mapNotNull { document ->
                val geoPoint = document.getGeoPoint(FirestoreKey.DOCUMENT_KEY_LOCATION)
                    ?: return@mapNotNull null

                // Location Lat/Lng Info
                val labelLat = geoPoint.latitude
                val labelLng = geoPoint.longitude

                // Location Data
                val location = Location(
                    lat = labelLat,
                    lng = labelLng,
                )

                // Only create Label if location exists
                val id = document.id
                val name = document.getString(FirestoreKey.DOCUMENT_KEY_COLOR) ?: ""
                val color = document.getString(FirestoreKey.DOCUMENT_KEY_NAME) ?: ""

                // Label Data
                Label(
                    id = id,
                    name = name,
                    color = color,
                    location = location,
                )
            }

            // Group mapmos by labelID
            val grouped = mapmoList.groupBy { it.labelID }

            // Combine each group with its corresponding label
            val listItem = grouped.mapNotNull { (labelID, mapmoList) ->
                val label = labels.find { it.id == labelID }
                MapmoListItem(
                    labelItem = label,
                    mapmoList = mapmoList,
                )
            }

            val mapmoListResult = MapmoList(
                count = mapmoList.size,
                list = listItem,
            )

            // Store in cache
            mapmoListCache = mapmoListResult

            // Return final MapmoList result
            return mapmoListResult
        } catch (e: Exception) {
            e.printStackTrace()
            // Return null if an error occurs
            return null
        }
    }

}