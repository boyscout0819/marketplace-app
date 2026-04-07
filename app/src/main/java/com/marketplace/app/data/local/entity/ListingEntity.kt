// entity/ListingEntity.kt
package com.marketplace.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.marketplace.app.domain.model.Listing
import java.util.Date
import java.util.UUID

@Entity(tableName = "listings")
data class ListingEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUrls: List<String>,
    val isFavorite: Boolean = false,
    val isSynced: Boolean = false,
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val version: Int = 1
) {
    fun toDomain() = Listing(
        id = id,
        title = title,
        description = description,
        price = price,
        category = category,
        imageUrls = imageUrls,
        isFavorite = isFavorite,
        createdAt = createdAt,
        updatedAt = updatedAt,
        version = version
    )
}

fun Listing.toEntity() = ListingEntity(
    id = id,
    title = title,
    description = description,
    price = price,
    category = category,
    imageUrls = imageUrls,
    isFavorite = isFavorite,
    createdAt = createdAt,
    updatedAt = updatedAt,
    version = version
)

enum class SyncStatus {
    PENDING, SYNCED, FAILED, CONFLICT
}


