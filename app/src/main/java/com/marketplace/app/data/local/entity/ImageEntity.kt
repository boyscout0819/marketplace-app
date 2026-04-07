package com.marketplace.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "images")
data class ImageEntity(
    @PrimaryKey
    val url: String,
    val listingId: String,
    val localPath: String?,
    val thumbnailPath: String?,
    val isUploaded: Boolean = false
)
