package com.mine.governance.data.repository

import com.mine.governance.data.local.dao.EmergencyDao
import com.mine.governance.data.local.entity.EmergencyReportEntity
import com.mine.governance.domain.engine.AiRiskAssessment
import com.mine.governance.domain.engine.AiRiskEngine
import com.mine.governance.domain.model.EmergencyType
import com.mine.governance.domain.model.NetworkMode
import com.mine.governance.domain.model.Severity
import com.mine.governance.domain.model.SyncPriorityLevels
import com.mine.governance.domain.model.SyncStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

data class EmergencySubmissionResult(
    val report: EmergencyReportEntity,
    val riskAssessment: AiRiskAssessment,
    val wasQueuedOffline: Boolean
)

class EmergencyRepository(
    private val emergencyDao: EmergencyDao,
    private val syncRepository: SyncRepository
) {

    val allEmergenciesFlow: Flow<List<EmergencyReportEntity>> = emergencyDao.getAllEmergenciesFlow()
    val pendingEmergencyCountFlow: Flow<Int> = emergencyDao.getPendingEmergencyCountFlow()

    suspend fun submitEmergencyReport(
        incidentType: EmergencyType,
        severity: Severity,
        title: String,
        description: String,
        immediateObservations: String,
        affectedPersonnel: Int,
        remarks: String,
        latitude: Double,
        longitude: Double,
        mineSector: String,
        officerEmployeeId: String,
        mediaUris: List<String>,
        currentNetworkMode: NetworkMode
    ): EmergencySubmissionResult = withContext(Dispatchers.IO) {
        val incidentId = "EMG-" + UUID.randomUUID().toString().take(8).uppercase()

        // 1. Evaluate Edge AI Risk
        val riskAssessment = AiRiskEngine.evaluateEmergencyRisk(
            type = incidentType,
            severity = severity,
            affectedPersonnel = affectedPersonnel,
            sector = mineSector
        )

        // 2. Determine initial sync status based on connectivity
        val isOfflineOrWeak = currentNetworkMode != NetworkMode.ONLINE
        val initialSyncStatus = if (isOfflineOrWeak) SyncStatus.PENDING else SyncStatus.SYNCED

        val reportEntity = EmergencyReportEntity(
            id = incidentId,
            incidentType = incidentType,
            severity = severity,
            title = title.ifBlank { "${incidentType.displayName} in $mineSector" },
            description = description,
            immediateObservations = immediateObservations,
            affectedPersonnel = affectedPersonnel,
            remarks = remarks,
            latitude = latitude,
            longitude = longitude,
            mineSector = mineSector,
            reportedByEmployeeId = officerEmployeeId,
            timestamp = System.currentTimeMillis(),
            aiRiskScore = riskAssessment.score,
            aiRationale = riskAssessment.rationale,
            mediaUris = mediaUris,
            syncStatus = initialSyncStatus,
            syncPriority = SyncPriorityLevels.EMERGENCY
        )

        // 3. Atomically write to SQLite
        emergencyDao.insertEmergency(reportEntity)

        // 4. If offline/weak network, push into Priority 1 outbox queue
        if (isOfflineOrWeak) {
            val payload = """
                {"id":"$incidentId","type":"${incidentType.name}","severity":"${severity.name}","score":${riskAssessment.score},"sector":"$mineSector"}
            """.trimIndent()

            syncRepository.enqueueItem(
                entityType = "EMERGENCY",
                entityId = incidentId,
                payloadJson = payload,
                priority = SyncPriorityLevels.EMERGENCY
            )
        }

        EmergencySubmissionResult(
            report = reportEntity,
            riskAssessment = riskAssessment,
            wasQueuedOffline = isOfflineOrWeak
        )
    }
}
