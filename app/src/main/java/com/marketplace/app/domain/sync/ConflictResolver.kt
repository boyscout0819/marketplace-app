package com.marketplace.app.domain.sync

import com.marketplace.app.domain.model.Listing
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConflictResolver @Inject constructor() {

    fun resolve(local: Listing, remote: Listing): Listing? {
        // Last-Write-Wins strategy with version check
        return when {
            remote.version > local.version -> {
                // Remote is newer, take remote but preserve local favorites
                remote.copy(isFavorite = local.isFavorite)
            }
            local.version > remote.version -> {
                // Local is newer, keep local
                local
            }
            local.updatedAt.after(remote.updatedAt) -> {
                // Same version but local is newer
                local
            }
            remote.updatedAt.after(local.updatedAt) -> {
                // Remote is newer
                remote.copy(isFavorite = local.isFavorite)
            }
            else -> {
                // Merge strategy: combine non-conflicting fields
                local.copy(
                    title = remote.title,
                    description = remote.description,
                    price = remote.price,
                    category = remote.category,
                    updatedAt = Date()
                )
            }
        }
    }
}