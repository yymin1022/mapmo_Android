package com.a6w.memo.data.repository

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
 */
class MapmoListRepositoryImpl: MapmoListRepository {
    private val db = FirebaseFirestore.getInstance()
    // Firestore document field keys
    companion object {
        private const val DOCUMENT_KEY_COLOR = "color"
        private const val DOCUMENT_KEY_NAME = "name"
        private const val DOCUMENT_KEY_UPDATED_AT = "updatedAt"
        private const val DOCUMENT_KEY_CONTENT = "content"
        private const val DOCUMENT_KEY_USER_ID = "userID"
        private const val DOCUMENT_KEY_LOCATION = "location"
        private const val DOCUMENT_KEY_IS_NOTIFY_ENABLED = "isNotifyEnabled"
        private const val DOCUMENT_KEY_LABEL_ID = "labelID"
        private const val FIRESTORE_KEY_COLLECTION_LABEL = "label"
        private const val FIRESTORE_KEY_COLLECTION_MAPMO = "mapmo"
    }
    override suspend fun getMapmoList(userID: String): MapmoList? {
        try {
            // Fetch all mapmo documents that belong to the given userID
            val snapshot = db.collection(FIRESTORE_KEY_COLLECTION_MAPMO)
                .whereEqualTo(DOCUMENT_KEY_USER_ID, userID)
                .get()
                .await()

            // Convert Firestore documents into a list of Mapmo objects
            val mapmoList = snapshot.documents.mapNotNull { document ->
                // Skip if GeoPoint is missing (invalid Mapmo data)
                val geoPoint = document.getGeoPoint(DOCUMENT_KEY_LOCATION) ?: return@mapNotNull null

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
                val content = document.getString(DOCUMENT_KEY_CONTENT) ?: ""
                val isNotifyEnabled = document.getBoolean(DOCUMENT_KEY_IS_NOTIFY_ENABLED) ?: false
                val labelID = document.getString(DOCUMENT_KEY_LABEL_ID) // nullable
                val updatedAt = document.getTimestamp(DOCUMENT_KEY_UPDATED_AT)?.seconds ?: 0
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
            val labelSnapshot = db.collection(FIRESTORE_KEY_COLLECTION_LABEL)
                .whereEqualTo(DOCUMENT_KEY_USER_ID, userID)
                .get()
                .await()

            // Convert Firestore documents into Label objects
            val labels = labelSnapshot.documents.mapNotNull { document ->
                val geoPoint = document.getGeoPoint(DOCUMENT_KEY_LOCATION) ?: return@mapNotNull null

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
                val name =  document.getString(DOCUMENT_KEY_COLOR) ?: ""
                val color =  document.getString(DOCUMENT_KEY_NAME) ?: ""

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

            // Return final MapmoList result
            return MapmoList(
                count = mapmoList.size,
                list = listItem,
            )

        } catch (e: Exception) {
            e.printStackTrace()
            // Return empty result if an error occurs
            return MapmoList(
                count = 0,
                list = emptyList(),
            )
        }
    }

}