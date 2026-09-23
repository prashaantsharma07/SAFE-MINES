package com.mine.governance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mine.governance.domain.model.Severity
import com.mine.governance.domain.model.SyncPriorityLevels
import com.mine.governance.domain.model.SyncStatus
import com.mine.governance.domain.model.TaskStatus

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val issueDescription: String,
    val originalReportId: String,
    val priority: Severity,
    val locationSector: String,
    val assignedBy: String,
    val assignedDate: Long,
    val deadlineDate: Long,
    val status: TaskStatus,
    val aiRiskScore: Int,
    val requiredAction: String,
    val beforeMediaUris: List<String> = emptyList(),
    val afterMediaUris: List<String> = emptyList(),
    val officerComments: String = "",
    val syncStatus: SyncStatus = SyncStatus.SYNCED,
    val syncPriority: Int = SyncPriorityLevels.CRITICAL_TASK
)
