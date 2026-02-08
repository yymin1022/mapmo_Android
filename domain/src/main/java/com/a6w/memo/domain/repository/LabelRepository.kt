package com.a6w.memo.domain.repository

import com.a6w.memo.domain.model.Label
import com.a6w.memo.domain.model.LabelList
/**
 * LabelRepository interface
 *
 * Repository for label CRUD operations and fetching all labels
 */
interface LabelRepository {
    // Fetch all label list by userID
    suspend fun getLabelList(userID:String): LabelList?
    // Fetch label list by userID & labelID
    suspend fun getLabel(labelID:String, userID:String):Label?
    // Add mapmo Label
    suspend fun addLabel(userID : String, labelContent: Label): Boolean
    // update mapmo label
    suspend fun updateLabel(labelID:String, labelContent :Label):Boolean
    // Delete mapmo label
    suspend fun deleteLabel(labelID:String, userID:String):Boolean
}