package com.marketplace.app.data.local.dao

import androidx.room.*
import com.marketplace.app.data.local.entity.PendingOperationEntity

@Dao
interface PendingOperationDao {
    @Insert
    suspend fun insertOperation(operation: PendingOperationEntity)

    @Query("SELECT * FROM pending_operations ORDER BY createdAt ASC")
    suspend fun getAllPendingOperations(): List<PendingOperationEntity>

    @Delete
    suspend fun deleteOperation(operation: PendingOperationEntity)

    @Query("UPDATE pending_operations SET retryCount = retryCount + 1 WHERE id = :operationId")
    suspend fun incrementRetryCount(operationId: Long)
}