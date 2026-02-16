package com.a6w.memo.data.repository

import com.a6w.memo.data.firebase.FirestoreKey
import com.a6w.memo.domain.model.Label
import com.a6w.memo.domain.model.LabelList
import com.a6w.memo.domain.model.Location
import com.a6w.memo.domain.repository.LabelRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.tasks.await

/**
 * LabelRepositoryImpl
 *
 * - Repository implementation for managing Label data in Firestore
 * - Provides CRUD operations and label list retrieval for a specific user
 *
 * Responsibilities:
 * - Fetch label list by user ID
 * - Fetch single label
 * - Add new label
 * - Update existing label
 * - Delete label
 *
 **/
class LabelRepositoryImpl: LabelRepository {
    private val firestoreDB = FirebaseFirestore.getInstance()
    private val labelCollection = firestoreDB.collection(FirestoreKey.COLLECTION_KEY_LABEL)
    override suspend fun getLabelList(userID: String): LabelList? {
        try {

            // Fetch all label documents that belong to the given userID
            val snapshot = labelCollection
                .whereEqualTo(FirestoreKey.DOCUMENT_KEY_USER_ID, userID)
                .get()
                .await()

            val labels = snapshot.documents.mapNotNull { document ->
                val geoPoint = document.getGeoPoint("location")
                // Convert GeoPoint to Location model
                val location = if (geoPoint != null) {

                    // Location Lat/Lng Info
                    val labelLat = geoPoint.latitude
                    val labelLng = geoPoint.longitude

                    // Location Data
                    Location(
                        lat = labelLat,
                        lng = labelLng,
                    )
                } else {
                    null
                }
                if (location != null) {
                    // Extract label fields and map Firestore document to Label model
                    val labelID = document.id
                    val labelName = document.getString(FirestoreKey.DOCUMENT_KEY_NAME) ?: ""
                    val labelColor = document.getString(FirestoreKey.DOCUMENT_KEY_COLOR) ?: ""
                    // Label Data
                    Label(
                        id = labelID,
                        name = labelName,
                        color = labelColor,
                        location = location
                    )
                } else {
                    null
                }
            }
            return LabelList(
                list = labels
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun getLabel(
        labelID: String,
        userID: String
    ): Label? {
        try {
            // Retrieve the label document by ID
            val document = labelCollection
                .document(labelID)
                .get()
                .await()
            if (document.exists()) {
                // Validate that the label belongs to the requesting user
                val documentUserID = document.getString(FirestoreKey.DOCUMENT_KEY_USER_ID)
                if (documentUserID != userID) {
                    return null
                }
                // Extract GeoPoint and convert it to domain Location
                val geoPoint =
                    document.getGeoPoint(FirestoreKey.DOCUMENT_KEY_LOCATION) ?: return null

                // Location Lat/Lng Info
                val mapmoLat = geoPoint.latitude
                val mapmoLng = geoPoint.longitude

                // Location Data
                val location = Location(
                    lat = mapmoLat,
                    lng = mapmoLng,
                )

                // Map Firestore fields to domain model
                val labelID = document.id
                val labelName = document.getString(FirestoreKey.DOCUMENT_KEY_NAME) ?: ""
                val labelColor = document.getString(FirestoreKey.DOCUMENT_KEY_COLOR) ?: ""

                return Label(
                    id = labelID,
                    name = labelName,
                    color = labelColor,
                    location = location,
                )
            } else {
                return null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun addLabel(
        userID: String,
        labelContent: Label
    ): Boolean {
        try {
            // Convert Location to Firestore GeoPoint
            val location = GeoPoint(labelContent.location.lat, labelContent.location.lng)

            // Label data to be stored in Firestore
            val labelData = hashMapOf(
                FirestoreKey.DOCUMENT_KEY_COLOR to labelContent.color,
                FirestoreKey.DOCUMENT_KEY_LOCATION to location,
                FirestoreKey.DOCUMENT_KEY_NAME to labelContent.name,
                FirestoreKey.DOCUMENT_KEY_USER_ID to userID,
            )

            // Add a new label document
            labelCollection
                .add(labelData)
                .await()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun updateLabel(
        labelID: String,
        labelContent: Label
    ): Boolean {
        try {
            // Convert domain Location object to Firestore GeoPoint
            val location = GeoPoint(labelContent.location.lat, labelContent.location.lng)

            // Prepare updated fields for the existing label
            val labelData = mapOf(
                FirestoreKey.DOCUMENT_KEY_COLOR to labelContent.color,
                FirestoreKey.DOCUMENT_KEY_LOCATION to location,
                FirestoreKey.DOCUMENT_KEY_NAME to labelContent.name,
            )

            // Update the existing label document
            labelCollection
                .document(labelContent.id)
                .update(labelData)
                .await()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun deleteLabel(
        labelID: String,
        userID: String
    ): Boolean {
        try {
            // Retrieve the label document to validate ownership
            val document = labelCollection
                .document(labelID)
                .get()
                .await()

            // Ensure that only the owner can delete the label
            if (document.getString(FirestoreKey.DOCUMENT_KEY_USER_ID) != userID) {
                return false
            }

            // Delete the label document
            labelCollection
                .document(labelID)
                .delete()
                .await()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}