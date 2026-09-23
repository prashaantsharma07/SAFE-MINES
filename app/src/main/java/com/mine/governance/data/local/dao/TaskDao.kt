package com.mine.governance.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mine.governance.data.local.entity.TaskEntity
import com.mine.governance.domain.model.SyncStatus
import com.mine.governance.domain.model.TaskStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskEntity>)

    @Query("SELECT * FROM tasks ORDER BY deadlineDate ASC")
    fun getAllTasksFlow(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: String): TaskEntity?

    @Query("SELECT COUNT(*) FROM tasks WHERE status != 'CLOSED'")
    fun getActiveTaskCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE priority = 'CRITICAL' AND status != 'CLOSED'")
    fun getCriticalTaskCountFlow(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE deadlineDate < :now AND status != 'CLOSED'")
    fun getOverdueTaskCountFlow(now: Long = System.currentTimeMillis()): Flow<Int>

    @Query("UPDATE tasks SET status = :status, syncStatus = :syncStatus WHERE id = :taskId")
    suspend fun updateTaskStatus(taskId: String, status: TaskStatus, syncStatus: SyncStatus = SyncStatus.PENDING)

    @Query("""
        UPDATE tasks 
        SET status = :status, 
            afterMediaUris = :afterUris, 
            officerComments = :comments, 
            syncStatus = :syncStatus 
        WHERE id = :taskId
    """)
    suspend fun completeTask(
        taskId: String,
        status: TaskStatus = TaskStatus.AWAITING_VERIFICATION,
        afterUris: List<String>,
        comments: String,
        syncStatus: SyncStatus = SyncStatus.PENDING
    )
}
