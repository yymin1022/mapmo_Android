package com.a6w.memo.domain.model

data class Mapmo(
    val mapmoId : String,
    val content: String,
    val isNotifyEnabled : Boolean,
    val labelName: String,
    val labelColor: String,
    val location : Location,
    val updatedAt : Long,
)



