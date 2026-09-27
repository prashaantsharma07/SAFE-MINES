package com.mine.governance.data.repository

import com.mine.governance.data.local.dao.TaskDao
import com.mine.governance.data.local.entity.TaskEntity
import com.mine.governance.domain.model.NetworkMode
import com.mine.governance.domain.model.Severity
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

    suspend fun ensureDefaultTasks() = withContext(Dispatchers.IO) {
        if (taskDao.getTaskCount() == 0) {
            val sampleTasks = listOf(
                TaskEntity(
                    id = "TSK-1049",
                    title = "Ventilation Shaft Damper Calibration",
                    issueDescription = "Airflow sensor reading lower than 4.5 m/s threshold in Sector 3 East Drift. Possible methane accumulation.",
                    originalReportId = "OBS-901",
                    priority = Severity.CRITICAL,
                    locationSector = "Sector 3 (East Drift)",
                    assignedBy = "Chief Inspector A. Verma",
                    assignedDate = System.currentTimeMillis() - 7200000,
                    deadlineDate = System.currentTimeMillis() + 14400000,
                    status = TaskStatus.PENDING,
                    aiRiskScore = 92,
                    requiredAction = "Inspect booster fan secondary baffles and measure CH4 concentration before recalibrating.",
                    beforeMediaUris = listOf("https://images.unsplash.com/photo-1578328819058-b69f3a3b0f6b?w=400"),
                    syncStatus = SyncStatus.SYNCED
                ),
                TaskEntity(
                    id = "TSK-1050",
                    title = "Hydraulic Roof Support Bolt Inspection",
                    issueDescription = "Fissure observed along shearer conveyor roadway rib-line at 420m depth.",
                    originalReportId = "OBS-884",
                    priority = Severity.CRITICAL,
                    locationSector = "Deep Extraction Face 2",
                    assignedBy = "Mine Manager D. Sen",
                    assignedDate = System.currentTimeMillis() - 18000000,
                    deadlineDate = System.currentTimeMillis() + 7200000,
                    status = TaskStatus.IN_PROGRESS,
                    aiRiskScore = 88,
                    requiredAction = "Deploy tell-tale extensometers and verify anchor tension on canopy props.",
                    beforeMediaUris = listOf("https://images.unsplash.com/photo-1541888946425-d0fbb186c5f8?w=400"),
                    syncStatus = SyncStatus.SYNCED
                ),
                TaskEntity(
                    id = "TSK-1051",
                    title = "Water Inrush Sump Pump Maintenance",
                    issueDescription = "Drainage valve 4B exhibiting pressure fluctuation in lower haulage sump.",
                    originalReportId = "OBS-879",
                    priority = Severity.HIGH,
                    locationSector = "Lower Haulage Level 5",
                    assignedBy = "Mechanical Lead S. Roy",
                    assignedDate = System.currentTimeMillis() - 86400000,
                    deadlineDate = System.currentTimeMillis() + 36000000,
                    status = TaskStatus.PENDING,
                    aiRiskScore = 67,
                    requiredAction = "Flush strainer and inspect impeller cavitation.",
                    syncStatus = SyncStatus.SYNCED
                ),
                TaskEntity(
                    id = "TSK-1052",
                    title = "Belt Conveyor Emergency Pull-Cord Test",
                    issueDescription = "Scheduled bi-weekly test of safety trip wires along Main Trunk Conveyor 1.",
                    originalReportId = "SCHED-22",
                    priority = Severity.MEDIUM,
                    locationSector = "Main Incline Beltway",
                    assignedBy = "Safety Operations",
                    assignedDate = System.currentTimeMillis() - 172800000,
                    deadlineDate = System.currentTimeMillis() + 86400000,
                    status = TaskStatus.PENDING,
                    aiRiskScore = 45,
                    requiredAction = "Walk entire 800m incline and trigger test trip switches.",
                    syncStatus = SyncStatus.SYNCED
                ),
                TaskEntity(
                    id = "TSK-1045",
                    title = "Dust Suppression Water Sprinklers Replacement",
                    issueDescription = "Nozzles 8 and 9 clogged with mineral residue at Transfer Chute 3.",
                    originalReportId = "OBS-862",
                    priority = Severity.LOW,
                    locationSector = "Transfer Chute 3",
                    assignedBy = "Environmental Safety",
                    assignedDate = System.currentTimeMillis() - 259200000,
                    deadlineDate = System.currentTimeMillis() - 3600000,
                    status = TaskStatus.PENDING,
                    aiRiskScore = 38,
                    requiredAction = "Replace ceramic misting tips and verify 3.5 bar water pressure.",
                    syncStatus = SyncStatus.SYNCED
                )
            )
            taskDao.insertAll(sampleTasks)
        }
    }

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

    suspend fun insertSimulationTasks() = withContext(Dispatchers.IO) {
        val simTask1 = TaskEntity(
            id = "SIM-TSK-401",
            title = "DRILL: Face 2 Methane Evacuation Protocol",
            issueDescription = "Verify complete withdrawal of 14 miners via intake drift and activate auxiliary compressed-air curtains.",
            originalReportId = "SIM-EMG-9021",
            priority = Severity.CRITICAL,
            locationSector = "Shaft 4 • Deep Extraction Face 2",
            assignedBy = "DGMS Drill Coordinator",
            assignedDate = System.currentTimeMillis(),
            deadlineDate = System.currentTimeMillis() + 1800000,
            status = TaskStatus.PENDING,
            aiRiskScore = 94,
            requiredAction = "Deploy air curtains, take roll call at fresh air base, and isolate section power.",
            syncStatus = SyncStatus.PENDING
        )
        val simTask2 = TaskEntity(
            id = "SIM-TSK-402",
            title = "DRILL: Return Airway Methanometer Cross-Check",
            issueDescription = "Take dual readings at 10m intervals along return airway #3 with calibrated optical methanometer.",
            originalReportId = "SIM-EMG-9021",
            priority = Severity.HIGH,
            locationSector = "Return Airway #3",
            assignedBy = "Safety Overman",
            assignedDate = System.currentTimeMillis(),
            deadlineDate = System.currentTimeMillis() + 3600000,
            status = TaskStatus.PENDING,
            aiRiskScore = 82,
            requiredAction = "Log percentage at 5 minute intervals and transmit reading via leaky feeder radio.",
            syncStatus = SyncStatus.PENDING
        )
        taskDao.insertAll(listOf(simTask1, simTask2))
    }

    suspend fun clearSimulationTasks() = withContext(Dispatchers.IO) {
        taskDao.clearSimulatedTasks()
    }
}
