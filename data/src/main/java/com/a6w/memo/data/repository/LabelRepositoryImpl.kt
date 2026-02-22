package com.a6w.memo.data.repository

import com.a6w.memo.data.firebase.FirestoreKey
import com.a6w.memo.domain.model.Label
import com.a6w.memo.domain.model.LabelList
import com.a6w.memo.domain.model.Location
import com.a6w.memo.domain.repository.LabelRepository
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
    private val labelCollection by lazy { firestoreDB.collection(FirestoreKey.COLLECTION_KEY_LABEL) }

    // In-memory cache for LabelList
    private var labelListCache = mutableMapOf<String, LabelList>()

    // Individual Label cache keyed by labelID
    private val labelCache = mutableMapOf<String, Label>()

    override suspend fun getLabelList(userID: String): LabelList? {
        try {
            // Return cache if available
            labelListCache[userID]?.let { return it }

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
                        location = location,
                    )
                } else {
                    null
                }
            }
            // Create a LabelList result object from fetched labels
            val labelListResult = LabelList(
                list = labels
            )
            // Cache the full label list
            labelListCache[userID] = labelListResult
            // Populate individual label cache for quick lookup by ID
            labels.forEach {
                labelCache[it.id] = it
            }
            return labelListResult
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
            // Return cached label if available
            labelCache[labelID]?.let { return it }
            // Retrieve the label document by ID
            val document = labelCollection
                .document(labelID)
                .get()
                .await()
            if (document.exists()) {
                val cachedLabelData = labelCache[labelID]
                if (cachedLabelData != null) {
                    return cachedLabelData
                }
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

                // Create a Label result object from fetched label
                val labelResult = Label(
                    id = labelID,
                    name = labelName,
                    color = labelColor,
                    location = location,
                )

                // Cache the label
                labelCache[labelID] = labelResult
                return labelResult
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
            val addedLabelRef = labelCollection
                .add(labelData)
                .await()

            val addedLabelID = addedLabelRef.id
            val newLabel = labelContent.copy(id = addedLabelID)

            // Update in-memory caches to keep local state consistent
            labelCache[addedLabelID] = newLabel
            labelListCache[userID] = labelListCache[userID]?.let {
                it.copy(list = it.list + newLabel)
            } ?: LabelList(list = listOf(newLabel))
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun updateLabel(
        labelID: String,
        labelContent: Label,
        userID: String
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

            // Update single label cache
            labelCache[labelID] = labelContent

            // Replace updated label in list cache (if exists)
            labelListCache[userID] = labelListCache[userID]?.let {
                it.copy(list = it.list + labelContent)
            } ?: LabelList(list = listOf(labelContent))
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

            // Remove from label cache
            labelCache.remove(labelID)

            // Remove label from list cache (if exists)
            labelListCache[userID]?.let { currentCache ->
                labelListCache[userID] = currentCache.copy(
                    list = currentCache.list.filter { it.id != labelID }
                )
            }
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}