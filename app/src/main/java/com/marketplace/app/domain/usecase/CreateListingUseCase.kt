package com.marketplace.app.domain.usecase

import com.marketplace.app.data.repository.ListingRepository
import com.marketplace.app.domain.model.ImageData
import com.marketplace.app.domain.model.Listing
import javax.inject.Inject

class CreateListingUseCase @Inject constructor(
    private val repository: ListingRepository
) {
    suspend operator fun invoke(listing: Listing, images: List<ImageData>): Result<String> {
        return repository.createListing(listing, images)
    }
}