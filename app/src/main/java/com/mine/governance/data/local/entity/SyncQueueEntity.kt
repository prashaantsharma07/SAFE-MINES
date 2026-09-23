package com.mine.governance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mine.governance.domain.model.SyncStatus

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true)
    val queueId: Long = 0,
    val entityType: String, // "EMERGENCY", "TASK_UPDATE", "OBSERVATION", "INSPECTION"
    val entityId: String,
    val payloadJson: String,
    val priority: Int, // 1 (Highest / Emergency) to 5 (Lowest / Telemetry)
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val retryCount: Int = 0,
    val createdAt: Long = System.currentTimeMillis(),
    val lastAttemptAt: Long? = null,
    val errorMessage: String? = null
)
