// repository/ListingRepository.kt
package com.marketplace.app.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton
import com.marketplace.app.data.local.dao.ListingDao
import com.marketplace.app.data.local.dao.PendingOperationDao
import com.marketplace.app.data.local.entity.*
import com.marketplace.app.data.remote.api.ApiService
import com.marketplace.app.data.remote.model.ListingRequest
import com.marketplace.app.domain.model.ImageData
import com.marketplace.app.domain.model.Listing
import com.marketplace.app.domain.sync.ConflictResolver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

@Singleton
class ListingRepository @Inject constructor(
    private val listingDao: ListingDao,
    private val pendingOperationDao: PendingOperationDao,
    private val apiService: ApiService,
    private val conflictResolver: ConflictResolver
) {

    fun getAllListings(): Flow<List<Listing>> {
        return listingDao.getAllListings().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getFavoriteListings(): Flow<List<Listing>> {
        return listingDao.getFavoriteListings().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun createListing(listing: Listing, images: List<ImageData>): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val entity = listing.toEntity()
                listingDao.insertListing(entity)

                // Queue operation for sync
                val operation = PendingOperationEntity(
                    operationType = OperationType.CREATE,
                    entityId = entity.id,
                    data = Json.encodeToString(ListingRequest.serializer(), ListingRequest.fromDomain(listing))
                )
                pendingOperationDao.insertOperation(operation)

                Result.success(entity.id)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateListing(listing: Listing): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val existing = listingDao.getListingById(listing.id)
                if (existing != null && existing.version >= listing.version) {
                    return@withContext Result.failure(ConflictException("Version conflict"))
                }

                val entity = listing.toEntity().copy(
                    version = (existing?.version ?: 0) + 1,
                    syncStatus = SyncStatus.PENDING
                )
                listingDao.updateListing(entity)

                val operation = PendingOperationEntity(
                    operationType = OperationType.UPDATE,
                    entityId = entity.id,
                    data = Json.encodeToString(ListingRequest.serializer(), ListingRequest.fromDomain(listing))
                )
                pendingOperationDao.insertOperation(operation)

                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun toggleFavorite(listingId: String, isFavorite: Boolean) {
        listingDao.updateFavoriteStatus(listingId, isFavorite)
    }

    fun getListingById(listingId: String): Flow<Listing?> {
        return listingDao.getListingByIdFlow(listingId).map { it?.toDomain() }
    }

    suspend fun deleteListing(listingId: String) {
        withContext(Dispatchers.IO) {
            listingDao.deleteListing(listingId)
            // Optionally, we could add a PENDING_DELETE operation here for sync
        }
    }

    suspend fun fetchAndSaveListings(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getListings()
                if (response.isSuccessful) {
                    response.body()?.let { remoteListings ->
                        remoteListings.forEach { remote ->
                            val existing = listingDao.getListingById(remote.id)
                            if (existing == null) {
                                val domain = remote.toDomain()
                                listingDao.insertListing(domain.toEntity().copy(syncStatus = SyncStatus.SYNCED))
                            }
                        }
                    }
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to fetch listings: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun syncWithRemote(): SyncResult {
        return withContext(Dispatchers.IO) {
            val pendingOps = pendingOperationDao.getAllPendingOperations()
            var successCount = 0
            var conflictCount = 0

            for (op in pendingOps) {
                try {
                    when (op.operationType) {
                        OperationType.CREATE -> {
                            val request = Json.decodeFromString(ListingRequest.serializer(), op.data)
                            val response = apiService.createListing(request)
                            if (response.isSuccessful) {
                                listingDao.updateSyncStatus(op.entityId, SyncStatus.SYNCED)
                                pendingOperationDao.deleteOperation(op)
                                successCount++
                            }
                        }
                        OperationType.UPDATE -> {
                            val request = Json.decodeFromString(ListingRequest.serializer(), op.data)
                            val response = apiService.updateListing(op.entityId, request)
                            if (response.isSuccessful) {
                                val remoteListing = response.body()
                                val localListing = listingDao.getListingById(op.entityId)

                                if (localListing != null && remoteListing != null) {
                                    val resolved = conflictResolver.resolve(localListing.toDomain(), remoteListing.toDomain())
                                    if (resolved != null) {
                                        listingDao.updateListing(resolved.toEntity())
                                        listingDao.updateSyncStatus(op.entityId, SyncStatus.SYNCED)
                                        pendingOperationDao.deleteOperation(op)
                                        successCount++
                                    } else {
                                        listingDao.updateSyncStatus(op.entityId, SyncStatus.CONFLICT)
                                        conflictCount++
                                    }
                                }
                            }
                        }
                        else -> {}
                    }
                } catch (e: Exception) {
                    pendingOperationDao.incrementRetryCount(op.id)
                    if (op.retryCount >= 3) {
                        listingDao.updateSyncStatus(op.entityId, SyncStatus.FAILED)
                    }
                }
            }

            SyncResult(successCount, conflictCount, pendingOps.size - successCount - conflictCount)
        }
    }
}

data class SyncResult(val synced: Int, val conflicts: Int, val failed: Int)
class ConflictException(message: String) : Exception(message)
