package com.mine.governance.domain.model

enum class EmergencyType(val displayName: String, val iconName: String) {
    FIRE("Fire Outbreak", "LocalFireDepartment"),
    GAS_LEAKAGE("Gas Leakage (CH4/CO)", "Warning"),
    VENTILATION_FAILURE("Ventilation Failure", "Air"),
    GROUND_INSTABILITY("Ground Instability / Roof Fall", "Terrain"),
    EQUIPMENT_FAILURE("Heavy Machinery Failure", "PrecisionManufacturing"),
    WORKER_INJURY("Worker Injury / Medical", "MedicalServices"),
    ENVIRONMENTAL_HAZARD("Environmental Hazard", "Dangerous"),
    OTHER("Other Critical Event", "ReportProblem")
}

enum class Severity(val displayName: String, val weight: Int) {
    CRITICAL("Critical", 4),
    HIGH("High", 3),
    MEDIUM("Medium", 2),
    LOW("Low", 1)
}

enum class TaskStatus(val displayName: String) {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    AWAITING_VERIFICATION("Awaiting Verification"),
    CLOSED("Closed")
}

enum class NetworkMode(val displayName: String) {
    ONLINE("Online"),
    WEAK_NETWORK("Weak / Mesh Mode"),
    OFFLINE("Offline")
}

enum class ObservationCategory(val displayName: String) {
    SAFETY("Safety Violation"),
    ENVIRONMENT("Environmental Issue"),
    CONTRACTOR_RELATED("Contractor Safety"),
    OTHER("Other Compliance")
}

enum class ComplianceStatus(val displayName: String) {
    COMPLIANT("Compliant"),
    OBSERVATION("Observation Noted"),
    VIOLATION("Safety Violation")
}

enum class SyncStatus {
    PENDING,
    SYNCING,
    SYNCED,
    FAILED
}

object SyncPriorityLevels {
    const val EMERGENCY = 1
    const val CRITICAL_TASK = 2
    const val OBSERVATION = 3
    const val INSPECTION = 4
    const val TELEMETRY_LOG = 5
}
