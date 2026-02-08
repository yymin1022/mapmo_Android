package com.a6w.memo.data.repository

import com.a6w.memo.domain.model.Label
import com.a6w.memo.domain.model.Location
import com.a6w.memo.domain.model.Mapmo
import com.a6w.memo.domain.model.MapmoList
import com.a6w.memo.domain.model.MapmoListItem
import com.a6w.memo.domain.repository.MapmoListRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MapmoListRepositoryImpl : MapmoListRepository {
    val db = FirebaseFirestore.getInstance()

    override suspend fun getMapmoList(userID: String): MapmoList? {
        try {
            // Fetch all mapmo documents that belong to the given userID
            val snapshot = db.collection("mapmo")
                .whereEqualTo("userID", userID)
                .get()
                .await()

            // Convert Firestore documents into a list of Mapmo objects
            val mapmos = snapshot.documents.mapNotNull { document ->
                val geoPoint = document.getGeoPoint("location") ?: return@mapNotNull null
                // Convert GeoPoint to Location model
                val location = Location(
                    lat = geoPoint.latitude,
                    lng = geoPoint.longitude,
                )

                val updatedAt = document.getTimestamp("updatedAt")

                // Only create Mapmo if location exists
                Mapmo(
                    mapmoID = document.id,
                    content = document.getString("content") ?: "",
                    isNotifyEnabled = document.getBoolean("isNotifyEnabled") ?: false,
                    labelID = document.getString("labelID"), // nullable 유지
                    location = location,
                    updatedAt = updatedAt?.seconds ?: 0,
                )


            }

            // Fetch all label documents for the user
            val labelSnapshot = db.collection("label")
                .whereEqualTo("userID", userID)
                .get()
                .await()

            // Convert Firestore documents into Label objects
            val labels = labelSnapshot.documents.mapNotNull { document ->
                val geoPoint = document.getGeoPoint("location") ?: return@mapNotNull null
                // Convert GeoPoint to Location model
                val location = Location(
                    lat = geoPoint.latitude,
                    lng = geoPoint.longitude,
                )

                // Only create Label if location exists
                Label(
                    id = document.id,
                    name = document.getString("name") ?: "",
                    color = document.getString("color") ?: "",
                    location = location,
                )

            }

            // Group mapmos by labelID
            val grouped = mapmos.groupBy { it.labelID }

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
                count = mapmos.size,
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