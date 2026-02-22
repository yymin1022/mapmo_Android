package com.a6w.memo.data.firebase

/**
 * FirestoreKey
 *
 * - Contains constant key values used for Firestore documents and collections
 * - Used to avoid hardcoded strings and prevent typo-related bugs
 *
 * @param COLLECTION_KEY_LABEL Firestore collection name for labels
 * @param COLLECTION_KEY_MAPMO Firestore collection name for mapmo documents
 * @param DOCUMENT_KEY_COLOR Field key for label color
 * @param DOCUMENT_KEY_NAME Field key for label name
 * @param DOCUMENT_KEY_UPDATED_AT Field key for last updated timestamp
 * @param DOCUMENT_KEY_CONTENT Field key for mapmo content
 * @param DOCUMENT_KEY_USER_ID Field key for owner user ID
 * @param DOCUMENT_KEY_LOCATION Field key for GeoPoint location
 * @param DOCUMENT_KEY_IS_NOTIFY_ENABLED Field key for notification status
 * @param DOCUMENT_KEY_LABEL_ID Field key for associated label ID
 *
 * */
object FirestoreKey {
    // Collection keys
    const val COLLECTION_KEY_LABEL = "label"
    const val COLLECTION_KEY_MAPMO = "mapmo"
    const val COLLECTION_KEY_USER = "user"

    // Document field keys
    const val DOCUMENT_KEY_COLOR = "color"
    const val DOCUMENT_KEY_NAME = "name"
    const val DOCUMENT_KEY_UPDATED_AT = "updatedAt"
    const val DOCUMENT_KEY_CONTENT = "content"
    const val DOCUMENT_KEY_USER_ID = "userID"
    const val DOCUMENT_KEY_LOCATION = "location"
    const val DOCUMENT_KEY_IS_NOTIFY_ENABLED = "isNotifyEnabled"
    const val DOCUMENT_KEY_LABEL_ID = "labelID"
    const val DOCUMENT_KEY_NICKNAME = "nickname"
    const val DOCUMENT_KEY_CREATED_AT = "createdAt"
}