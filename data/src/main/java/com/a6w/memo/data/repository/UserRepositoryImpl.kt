package com.a6w.memo.data.repository

import com.a6w.memo.data.firebase.FirestoreKey
import com.a6w.memo.domain.model.UserInfo
import com.a6w.memo.domain.repository.UserRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * UserRepositoryImpl
 *
 * - Repository implementation for managing UserInfo data in Firestore
 * - Provides CRUD operations for user account management
 * - Caches individual UserInfo to reduce redundant server calls
 *
 * Responsibilities:
 * - Fetch user info by userID
 * - Add new user
 * - Update user nickname
 * - Delete user account
 *
 */
class UserRepositoryImpl: UserRepository {
    private val firestoreDB = FirebaseFirestore.getInstance()
    private val userCollection = firestoreDB.collection(FirestoreKey.COLLECTION_KEY_USER)

    // Individual UserInfo cache keyed by userID
    private val userCache = mutableMapOf<String, UserInfo>()

    override suspend fun getUserInfo(userID: String): UserInfo? {
        try {
            // Return cache if available
            userCache[userID]?.let { return it }

            val document = userCollection
                .document(userID)
                .get()
                .await()

            if (!document.exists()) return null

            // Extract timestamp and convert to seconds
            val timestampCreatedAt =
                document.get(FirestoreKey.DOCUMENT_KEY_CREATED_AT) as? Timestamp
            val createdAtSeconds = timestampCreatedAt?.seconds ?: 0

            // Extract user fields and map Firestore document to UserInfo model
            val userId = document.id
            val nickname = document.getString(FirestoreKey.DOCUMENT_KEY_NICKNAME) ?: ""

            // UserInfo Data
            val userInfo = UserInfo(
                id = userId,
                nickName = nickname,
                createdAt = createdAtSeconds,
            )

            // Store in cache
            userCache[userID] = userInfo
            return userInfo
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override suspend fun addUser(userInfo: UserInfo): Boolean {
        try {
            val createdAt = Timestamp.now()

            val userData = hashMapOf(
                FirestoreKey.DOCUMENT_KEY_NICKNAME to userInfo.nickName,
                FirestoreKey.DOCUMENT_KEY_CREATED_AT to createdAt,
            )

            // Add new user document with specific userID
            val addedUserRef = userCollection
                .add(userData)
                .await()
            val addedUserID = addedUserRef.id

            // Update cache with server timestamp and UserID
            val addedUser = userInfo.copy(
                id = addedUserID,
                createdAt = createdAt.seconds
            )
            userCache[addedUserID] = addedUser

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun updateUserInfo(userID: String, newNickName: String): Boolean {
        try {
            val userData = mapOf(
                FirestoreKey.DOCUMENT_KEY_NICKNAME to newNickName
            )

            // Update user document
            userCollection
                .document(userID)
                .update(userData)
                .await()

            // Update cache
            userCache[userID]?.let { currentUser ->
                userCache[userID] = currentUser.copy(nickName = newNickName)
            }

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    override suspend fun deleteAccount(userID: String): Boolean {
        try {
            // Delete user document
            userCollection
                .document(userID)
                .delete()
                .await()

            // Remove from cache
            userCache.remove(userID)

            return true
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }
}