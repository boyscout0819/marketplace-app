package com.marketplace.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "pending_operations")
data class PendingOperationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val operationType: OperationType,
    val entityId: String,
    val data: String, // JSON string
    val retryCount: Int = 0,
    val createdAt: Date = Date()
)

enum class OperationType {
    CREATE, UPDATE, DELETE
}