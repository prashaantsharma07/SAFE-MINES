package com.mine.governance.data.repository

import com.mine.governance.data.local.dao.EmergencyDao
import com.mine.governance.data.local.dao.SyncQueueDao
import com.mine.governance.data.local.dao.TaskDao
import com.mine.governance.data.local.entity.SyncQueueEntity
import com.mine.governance.domain.model.SyncStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

data class SyncResult(
    val processedCount: Int,
    val successCount: Int,
    val failedCount: Int,
    val highestPrioritySynced: Int?
)

class SyncRepository(
    private val syncQueueDao: SyncQueueDao,
    private val emergencyDao: EmergencyDao,
    private val taskDao: TaskDao
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
     * 1: Emergency SOS
     * 2: Critical Tasks & Evidence
     * 3: Safety Observations
     * 4: Inspections
     * 5: Telemetry / Logs
     */
    suspend fun dispatchPendingQueue(isMeshGatewayOnly: Boolean = false): SyncResult = withContext(Dispatchers.IO) {
        val pendingItems = syncQueueDao.getPendingQueueOrderedByPriority()
        if (pendingItems.isEmpty()) {
            return@withContext SyncResult(0, 0, 0, null)
        }

        var successCount = 0
        var failedCount = 0
        var highestPriority: Int? = null

        for (item in pendingItems) {
            syncQueueDao.updateStatus(item.queueId, SyncStatus.SYNCING)

            try {
                // Simulate network transmission delay
                delay(300)

                // Dispatch depending on entity type
                when (item.entityType) {
                    "EMERGENCY" -> {
                        // Priority 1: Instant remote alert delivery
                        emergencyDao.updateSyncStatus(item.entityId, SyncStatus.SYNCED)
                    }
                    "TASK_UPDATE" -> {
                        // Priority 2: Task status / verification delivery
                        taskDao.updateTaskStatus(item.entityId, com.mine.governance.domain.model.TaskStatus.AWAITING_VERIFICATION, SyncStatus.SYNCED)
                    }
                    else -> {
                        // Generic entities
                    }
                }

                syncQueueDao.deleteById(item.queueId)
                successCount++
                if (highestPriority == null || item.priority < highestPriority) {
                    highestPriority = item.priority
                }
            } catch (e: Exception) {
                failedCount++
                syncQueueDao.recordFailure(item.queueId, e.message ?: "Network Gateway Timeout")
            }
        }

        SyncResult(
            processedCount = pendingItems.size,
            successCount = successCount,
            failedCount = failedCount,
            highestPrioritySynced = highestPriority
        )
    }
}
