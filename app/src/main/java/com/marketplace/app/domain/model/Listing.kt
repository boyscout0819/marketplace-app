package com.marketplace.app.domain.model

import java.util.Date

data class Listing(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val category: String,
    val imageUrls: List<String>,
    val isFavorite: Boolean = false,
    val createdAt: Date,
    val updatedAt: Date,
    val version: Int = 1
)