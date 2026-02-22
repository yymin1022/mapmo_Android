package com.a6w.memo.domain.repository

import com.a6w.memo.domain.model.UserInfo

/**
 * UserRepository interface
 *
 * Repository for user CRUD operations and user account management
 */
interface UserRepository {
    // Fetch user info by userID
    suspend fun getUserInfo(userID: String): UserInfo?
    // Add new user
    suspend fun addUser(userInfo: UserInfo): Boolean
    // Update user nickname
    suspend fun updateUserInfo(userID: String, newNickName: String): Boolean
    // Delete user account by userID
    suspend fun deleteAccount(userID: String): Boolean
}