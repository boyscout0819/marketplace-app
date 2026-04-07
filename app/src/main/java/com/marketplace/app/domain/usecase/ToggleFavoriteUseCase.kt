package com.marketplace.app.domain.usecase

import com.marketplace.app.data.repository.ListingRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val repository: ListingRepository
) {
    suspend operator fun invoke(listingId: String, isFavorite: Boolean) {
        repository.toggleFavorite(listingId, isFavorite)
    }
}
