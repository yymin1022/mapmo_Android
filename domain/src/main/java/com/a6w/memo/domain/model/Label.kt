package com.a6w.memo.domain.model

/**
 * Label model
 * - Label information for a user-selected location
 *
 */
data class Label(
    // label id
    val id: String,
    // label name
    val name: String,
    // label icon color
    val color:  String,
    // location of label
    val location: Location,
)
