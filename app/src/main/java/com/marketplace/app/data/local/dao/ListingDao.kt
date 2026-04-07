package com.marketplace.app.data.local.dao

import androidx.room.*
import com.marketplace.app.data.local.entity.ListingEntity
import com.marketplace.app.data.local.entity.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ListingDao {
    @Query("SELECT * FROM listings ORDER BY createdAt DESC")
    fun getAllListings(): Flow<List<ListingEntity>>

    @Query("SELECT * FROM listings WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteListings(): Flow<List<ListingEntity>>

    @Query("SELECT * FROM listings WHERE syncStatus = :syncStatus")
    suspend fun getListingsBySyncStatus(syncStatus: SyncStatus): List<ListingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertListing(listing: ListingEntity)

    @Update
    suspend fun updateListing(listing: ListingEntity)

    @Query("DELETE FROM listings WHERE id = :listingId")
    suspend fun deleteListing(listingId: String)

    @Query("SELECT * FROM listings WHERE id = :listingId")
    fun getListingByIdFlow(listingId: String): Flow<ListingEntity?>

    @Query("SELECT * FROM listings WHERE id = :listingId")
    suspend fun getListingById(listingId: String): ListingEntity?

    @Query("UPDATE listings SET syncStatus = :syncStatus WHERE id = :listingId")
    suspend fun updateSyncStatus(listingId: String, syncStatus: SyncStatus)

    @Query("UPDATE listings SET isFavorite = :isFavorite WHERE id = :listingId")
    suspend fun updateFavoriteStatus(listingId: String, isFavorite: Boolean)
}