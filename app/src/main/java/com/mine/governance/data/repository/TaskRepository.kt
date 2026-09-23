package com.mine.governance.data.repository

import com.mine.governance.data.local.dao.TaskDao
import com.mine.governance.data.local.entity.TaskEntity
import com.mine.governance.domain.model.NetworkMode
import com.mine.governance.domain.model.SyncPriorityLevels
import com.mine.governance.domain.model.SyncStatus
import com.mine.governance.domain.model.TaskStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class TaskRepository(
    private val taskDao: TaskDao,
    private val syncRepository: SyncRepository
) {

    val allTasksFlow: Flow<List<TaskEntity>> = taskDao.getAllTasksFlow()
    val activeTaskCountFlow: Flow<Int> = taskDao.getActiveTaskCountFlow()
    val criticalTaskCountFlow: Flow<Int> = taskDao.getCriticalTaskCountFlow()
    val overdueTaskCountFlow: Flow<Int> = taskDao.getOverdueTaskCountFlow()

    suspend fun getTaskById(taskId: String): TaskEntity? = withContext(Dispatchers.IO) {
        taskDao.getTaskById(taskId)
    }

    suspend fun startTask(taskId: String, networkMode: NetworkMode) = withContext(Dispatchers.IO) {
        val syncStatus = if (networkMode == NetworkMode.ONLINE) SyncStatus.SYNCED else SyncStatus.PENDING
        taskDao.updateTaskStatus(taskId, TaskStatus.IN_PROGRESS, syncStatus)

        if (networkMode != NetworkMode.ONLINE) {
            syncRepository.enqueueItem(
                entityType = "TASK_UPDATE",
                entityId = taskId,
                payloadJson = """{"id":"$taskId","status":"IN_PROGRESS"}""",
                priority = SyncPriorityLevels.CRITICAL_TASK
            )
        }
    }

    suspend fun completeTask(
        taskId: String,
        afterEvidenceUris: List<String>,
        comments: String,
        networkMode: NetworkMode
    ) = withContext(Dispatchers.IO) {
        val syncStatus = if (networkMode == NetworkMode.ONLINE) SyncStatus.SYNCED else SyncStatus.PENDING
        taskDao.completeTask(
            taskId = taskId,
            status = TaskStatus.AWAITING_VERIFICATION,
            afterUris = afterEvidenceUris,
            comments = comments,
            syncStatus = syncStatus
        )

        if (networkMode != NetworkMode.ONLINE) {
            syncRepository.enqueueItem(
                entityType = "TASK_UPDATE",
                entityId = taskId,
                payloadJson = """{"id":"$taskId","status":"AWAITING_VERIFICATION","comments":"$comments"}""",
                priority = SyncPriorityLevels.CRITICAL_TASK
            )
        }
    }
}
