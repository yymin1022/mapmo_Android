package com.a6w.memo.domain.model
/**
 * UserInfo model
 * - user Information
 */
data class UserInfo(
    // user unique id
    val id: String,
    // user-defined nickname
    val nickName: String,
    // user creation date and time
    val createdAt: Long,
)
