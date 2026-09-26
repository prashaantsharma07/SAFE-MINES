package com.mine.governance.data.repository

import com.mine.governance.data.local.dao.EmergencyDao
import com.mine.governance.data.local.dao.SyncQueueDao
import com.mine.governance.data.local.dao.TaskDao
import com.mine.governance.data.local.entity.SyncQueueEntity
import com.mine.governance.data.sync.NetworkMonitor
import com.mine.governance.domain.model.NetworkMode
import com.mine.governance.domain.model.SyncStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

data class SyncResult(
    val processedCount: Int,
    val successCount: Int,
    val failedCount: Int,
    val highestPrioritySynced: Int?,
    val offlineDeferred: Boolean = false
)

class SyncRepository(
    private val syncQueueDao: SyncQueueDao,
    private val emergencyDao: EmergencyDao,
    private val taskDao: TaskDao,
    private val networkMonitor: NetworkMonitor? = null
) {

    val pendingSyncCountFlow: Flow<Int> = syncQueueDao.getPendingCountFlow()

    suspend fun enqueueItem(
        entityType: String,
        entityId: String,
        payloadJson: String,
        priority: Int
    ): Long = withContext(Dispatchers.IO) {
        val entity = SyncQueueEntity(
            entityType = entityType,
            entityId = entityId,
            payloadJson = payloadJson,
            priority = priority,
            syncStatus = SyncStatus.PENDING
        )
        syncQueueDao.enqueue(entity)
    }

    /**
     * Executes outbox dispatch strictly ordered by Priority ASC:
     * 1: Emergency SOS (Subterranean Priority Alert)
     * 2: Critical Tasks & Evidence
     * 3: Safety Observations & DGMS Reports
     * 4: Inspections & Shift Handover
     * 5: Telemetry / Sensor Logs
     */
    suspend fun dispatchPendingQueue(
        isMeshGatewayOnly: Boolean = false,
        forceOfflineSimulate: Boolean = false
    ): SyncResult = withContext(Dispatchers.IO) {
        val currentMode = networkMonitor?.getCurrentNetworkMode() ?: NetworkMode.OFFLINE
        val pendingItems = syncQueueDao.getPendingQueueOrderedByPriority()

        if (pendingItems.isEmpty()) {
            return@withContext SyncResult(0, 0, 0, null, offlineDeferred = false)
        }

        // Honest offline check: If underground and disconnected, do not falsely claim cloud sync
        if (currentMode == NetworkMode.OFFLINE && !forceOfflineSimulate) {
            return@withContext SyncResult(
                processedCount = 0,
                successCount = 0,
                failedCount = 0,
                highestPrioritySynced = null,
                offlineDeferred = true
            )
        }

        var successCount = 0
        var failedCount = 0
        var highestPriority: Int? = null

        for (item in pendingItems) {
            syncQueueDao.updateStatus(item.queueId, SyncStatus.SYNCING)

            try {
                // Gateway transmission delay simulation
                delay(250)

                // Dispatch depending on entity type
                when (item.entityType) {
                    "EMERGENCY" -> {
                        emergencyDao.updateSyncStatus(item.entityId, SyncStatus.SYNCED)
                    }
                    "TASK_UPDATE" -> {
                        taskDao.updateTaskStatus(
                            item.entityId,
                            com.mine.governance.domain.model.TaskStatus.AWAITING_VERIFICATION,
                            SyncStatus.SYNCED
                        )
                    }
                    else -> {
                        // Generic observations / reports marked synced
                    }
                }

                syncQueueDao.deleteById(item.queueId)
                successCount++
                if (highestPriority == null || item.priority < highestPriority) {
                    highestPriority = item.priority
                }
            } catch (e: Exception) {
                failedCount++
                syncQueueDao.recordFailure(item.queueId, e.message ?: "Gateway Connection Timeout")
            }
        }

        SyncResult(
            processedCount = pendingItems.size,
            successCount = successCount,
            failedCount = failedCount,
            highestPrioritySynced = highestPriority,
            offlineDeferred = false
        )
    }
}
