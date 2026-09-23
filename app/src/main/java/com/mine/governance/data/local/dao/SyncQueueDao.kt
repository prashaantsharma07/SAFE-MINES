package com.mine.governance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mine.governance.data.local.entity.SyncQueueEntity
import com.mine.governance.domain.model.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncQueueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueue(item: SyncQueueEntity): Long

    /**
     * Retrieves all pending outbox queue items sorted strictly by Priority ASC (1=Emergency first),
     * and secondarily by timestamp (FIFO).
     */
    @Query("""
        SELECT * FROM sync_queue 
        WHERE syncStatus = 'PENDING' OR syncStatus = 'FAILED' 
        ORDER BY priority ASC, createdAt ASC
    """)
    suspend fun getPendingQueueOrderedByPriority(): List<SyncQueueEntity>

    @Query("SELECT COUNT(*) FROM sync_queue WHERE syncStatus != 'SYNCED'")
    fun getPendingCountFlow(): Flow<Int>

    @Query("UPDATE sync_queue SET syncStatus = :status, lastAttemptAt = :attemptTime WHERE queueId = :queueId")
    suspend fun updateStatus(queueId: Long, status: SyncStatus, attemptTime: Long = System.currentTimeMillis())

    @Query("""
        UPDATE sync_queue 
        SET retryCount = retryCount + 1, 
            lastAttemptAt = :attemptTime, 
            errorMessage = :error, 
            syncStatus = 'FAILED' 
        WHERE queueId = :queueId
    """)
    suspend fun recordFailure(queueId: Long, error: String, attemptTime: Long = System.currentTimeMillis())

    @Query("DELETE FROM sync_queue WHERE queueId = :queueId")
    suspend fun deleteById(queueId: Long)

    @Query("DELETE FROM sync_queue WHERE syncStatus = 'SYNCED'")
    suspend fun pruneSynced()
}
