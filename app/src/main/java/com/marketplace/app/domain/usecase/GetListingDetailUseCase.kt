package com.marketplace.app.domain.usecase

import com.marketplace.app.data.repository.ListingRepository
import com.marketplace.app.domain.model.Listing
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetListingDetailUseCase @Inject constructor(
    private val repository: ListingRepository
) {
    operator fun invoke(listingId: String): Flow<Listing?> {
        return repository.getListingById(listingId)
    }
}
