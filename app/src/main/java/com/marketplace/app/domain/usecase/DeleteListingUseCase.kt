package com.marketplace.app.domain.usecase

import com.marketplace.app.data.repository.ListingRepository
import javax.inject.Inject

class DeleteListingUseCase @Inject constructor(
    private val repository: ListingRepository
) {
    suspend operator fun invoke(listingId: String) {
        repository.deleteListing(listingId)
    }
}
