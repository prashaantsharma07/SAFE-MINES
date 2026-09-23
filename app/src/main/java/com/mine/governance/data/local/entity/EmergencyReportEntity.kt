package com.mine.governance.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mine.governance.domain.model.EmergencyType
import com.mine.governance.domain.model.Severity
import com.mine.governance.domain.model.SyncPriorityLevels
import com.mine.governance.domain.model.SyncStatus

@Entity(tableName = "emergency_reports")
data class EmergencyReportEntity(
    @PrimaryKey
    val id: String,
    val incidentType: EmergencyType,
    val severity: Severity,
    val title: String,
    val description: String,
    val immediateObservations: String,
    val affectedPersonnel: Int,
    val remarks: String,
    val latitude: Double,
    val longitude: Double,
    val mineSector: String,
    val reportedByEmployeeId: String,
    val timestamp: Long = System.currentTimeMillis(),
    val aiRiskScore: Int,
    val aiRationale: String,
    val mediaUris: List<String> = emptyList(),
    val syncStatus: SyncStatus = SyncStatus.PENDING,
    val syncPriority: Int = SyncPriorityLevels.EMERGENCY
)
