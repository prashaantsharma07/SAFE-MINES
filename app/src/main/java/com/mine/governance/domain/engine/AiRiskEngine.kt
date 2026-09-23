package com.mine.governance.domain.engine

import com.mine.governance.domain.model.EmergencyType
import com.mine.governance.domain.model.Severity
import kotlin.math.roundToInt

data class AiRiskAssessment(
    val score: Int, // 0 - 100
    val riskLevel: Severity,
    val rationale: String,
    val recommendedImmediateAction: String,
    val requiresEvacuationAlarm: Boolean
)

object AiRiskEngine {

    /**
     * Evaluates real-time AI Risk score based on subterranean parameters,
     * hazard coefficients, worker exposure, and atmospheric threat level.
     */
    fun evaluateEmergencyRisk(
        type: EmergencyType,
        severity: Severity,
        affectedPersonnel: Int,
        sector: String
    ): AiRiskAssessment {
        // 1. Hazard base coefficient (0.0 to 1.0)
        val hazardWeight = when (type) {
            EmergencyType.GAS_LEAKAGE -> 1.00 // Explosive Methane (CH4) or Toxic CO
            EmergencyType.FIRE -> 0.95
            EmergencyType.GROUND_INSTABILITY -> 0.90 // Roof fall risk
            EmergencyType.VENTILATION_FAILURE -> 0.85 // Asphyxiation hazard
            EmergencyType.WORKER_INJURY -> 0.70
            EmergencyType.EQUIPMENT_FAILURE -> 0.60
            EmergencyType.ENVIRONMENTAL_HAZARD -> 0.55
            EmergencyType.OTHER -> 0.50
        }

        // 2. Severity weight
        val severityWeight = when (severity) {
            Severity.CRITICAL -> 1.0
            Severity.HIGH -> 0.75
            Severity.MEDIUM -> 0.50
            Severity.LOW -> 0.25
        }

        // 3. Worker exposure factor
        val workerFactor = when {
            affectedPersonnel > 15 -> 1.0
            affectedPersonnel in 6..15 -> 0.8
            affectedPersonnel in 1..5 -> 0.5
            else -> 0.2
        }

        // 4. Sector vulnerability factor (Subterranean deep extraction zones have higher risk)
        val sectorRisk = if (sector.contains("Deep", ignoreCase = true) ||
            sector.contains("Pit 4", ignoreCase = true) ||
            sector.contains("Face", ignoreCase = true)
        ) 1.0 else 0.7

        // Multi-attribute composite score calculation
        val rawScore = (
            (severityWeight * 35.0) +
            (hazardWeight * 30.0) +
            (workerFactor * 20.0) +
            (sectorRisk * 15.0)
        )

        val score = rawScore.roundToInt().coerceIn(10, 100)

        val finalLevel = when {
            score >= 80 -> Severity.CRITICAL
            score >= 60 -> Severity.HIGH
            score >= 40 -> Severity.MEDIUM
            else -> Severity.LOW
        }

        val rationale = buildString {
            append("AI Assessment triggered by ${type.displayName}. ")
            if (affectedPersonnel > 0) {
                append("$affectedPersonnel miners potentially in hazard radius. ")
            }
            if (sectorRisk > 0.8) {
                append("Deep subterranean sector ($sector) exhibits amplified risk factors.")
            }
        }

        val action = when (finalLevel) {
            Severity.CRITICAL -> "IMMEDIATE EVACUATION of Sector $sector. Trigger automated blast doors & emergency siren."
            Severity.HIGH -> "Halt heavy machinery operations immediately. Dispatch Rapid Response Team."
            Severity.MEDIUM -> "Inspect localized isolation dampers. Dispatch specialized maintenance crew."
            Severity.LOW -> "Log in shift supervisor record. Schedule standard safety verification."
        }

        return AiRiskAssessment(
            score = score,
            riskLevel = finalLevel,
            rationale = rationale,
            recommendedImmediateAction = action,
            requiresEvacuationAlarm = score >= 85
        )
    }
}
