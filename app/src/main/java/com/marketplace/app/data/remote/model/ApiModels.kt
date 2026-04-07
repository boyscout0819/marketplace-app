package com.marketplace.app.data.remote.model

import com.marketplace.app.domain.model.Listing
import kotlinx.serialization.Serializable

@Serializable
data class ListingResponse(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUrls: List<String>,
    val createdAt: String,
    val updatedAt: String,
    val version: Int
) {
    fun toDomain() = Listing(
        id = id,
        title = title,
        description = description,
        price = price,
        category = category,
        imageUrls = imageUrls,
        createdAt = java.util.Date(), // Simplified for now
        updatedAt = java.util.Date(),
        version = version
    )
}

@Serializable
data class ListingRequest(
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUrls: List<String>,
    val version: Int
) {
    companion object {
        fun fromDomain(listing: Listing) = ListingRequest(
            title = listing.title,
            description = listing.description,
            price = listing.price,
            category = listing.category,
            imageUrls = listing.imageUrls,
            version = listing.version
        )
    }
}

@Serializable
data class ImageUploadResponse(
    val url: String,
    val thumbnailUrl: String
)
