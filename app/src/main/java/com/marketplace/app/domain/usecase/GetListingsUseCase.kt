package com.marketplace.app.domain.usecase

import com.marketplace.app.data.repository.ListingRepository
import com.marketplace.app.domain.model.Listing
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetListingsUseCase @Inject constructor(
    val repository: ListingRepository
) {
    operator fun invoke(): Flow<List<Listing>> = repository.getAllListings()
}