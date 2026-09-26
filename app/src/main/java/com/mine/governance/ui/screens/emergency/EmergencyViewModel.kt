package com.mine.governance.ui.screens.emergency

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mine.governance.data.location.LocationHelper
import com.mine.governance.data.location.LocationStatus
import com.mine.governance.data.repository.AuthRepository
import com.mine.governance.data.repository.EmergencyRepository
import com.mine.governance.data.repository.EmergencySubmissionResult
import com.mine.governance.data.sync.NetworkMonitor
import com.mine.governance.domain.engine.AiRiskAssessment
import com.mine.governance.domain.engine.AiRiskEngine
import com.mine.governance.domain.model.EmergencyType
import com.mine.governance.domain.model.NetworkMode
import com.mine.governance.domain.model.Severity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class EmergencyUiState(
    val selectedType: EmergencyType = EmergencyType.GAS_LEAKAGE,
    val selectedSeverity: Severity = Severity.CRITICAL,
    val title: String = "",
    val description: String = "",
    val immediateObservations: String = "",
    val affectedPersonnel: Int = 2,
    val additionalRemarks: String = "",
    val mineSector: String = "Shaft 4 • Deep Extraction Face 2",
    val latitude: Double = 23.7957,
    val longitude: Double = 86.4304,
    val accuracyMeters: Float = 0f,
    val isSubterraneanLocationFallback: Boolean = false,
    val locationStatus: LocationStatus = LocationStatus.Idle,
    val capturedEvidenceUris: List<String> = emptyList(),
    val reportingOfficerId: String = "EMP-7842",
    val reportingOfficerName: String = "Rajesh Kumar",
    val currentNetworkMode: NetworkMode = NetworkMode.OFFLINE,
    val isSubmitting: Boolean = false,
    val submissionResult: EmergencySubmissionResult? = null,
    val showSuccessDialog: Boolean = false,
    val dynamicAiRisk: AiRiskAssessment = AiRiskEngine.evaluateEmergencyRisk(
        EmergencyType.GAS_LEAKAGE,
        Severity.CRITICAL,
        2,
        "Shaft 4 • Deep Extraction Face 2"
    )
)

class EmergencyViewModel(
    private val emergencyRepository: EmergencyRepository,
    private val authRepository: AuthRepository,
    private val networkMonitor: NetworkMonitor,
    private val locationHelper: LocationHelper
) : ViewModel() {

    private val _uiState = MutableStateFlow(EmergencyUiState())
    val uiState: StateFlow<EmergencyUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.activeUserFlow.collect { user ->
                if (user != null) {
                    _uiState.update {
                        it.copy(
                            reportingOfficerId = user.employeeId,
                            reportingOfficerName = user.name
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            networkMonitor.networkModeFlow.collect { mode ->
                _uiState.update { it.copy(currentNetworkMode = mode) }
            }
        }

        viewModelScope.launch {
            locationHelper.status.collect { status ->
                _uiState.update { state ->
                    when (status) {
                        is LocationStatus.Acquired -> state.copy(
                            locationStatus = status,
                            latitude = status.latitude,
                            longitude = status.longitude,
                            accuracyMeters = status.accuracyMeters,
                            isSubterraneanLocationFallback = status.isSubterraneanFallback
                        )
                        else -> state.copy(locationStatus = status)
                    }
                }
            }
        }

        requestRealLocation()
    }

    fun requestRealLocation() {
        viewModelScope.launch {
            locationHelper.fetchCurrentLocation()
        }
    }

    fun onLocationPermissionDenied() {
        locationHelper.setPermissionDenied()
    }

    fun selectType(type: EmergencyType) {
        _uiState.update {
            val updated = it.copy(selectedType = type)
            updated.copy(dynamicAiRisk = recomputeRisk(updated))
        }
    }

    fun selectSeverity(severity: Severity) {
        _uiState.update {
            val updated = it.copy(selectedSeverity = severity)
            updated.copy(dynamicAiRisk = recomputeRisk(updated))
        }
    }

    fun onTitleChange(title: String) = _uiState.update { it.copy(title = title) }
    fun onDescriptionChange(desc: String) = _uiState.update { it.copy(description = desc) }
    fun onImmediateObservationsChange(obs: String) = _uiState.update { it.copy(immediateObservations = obs) }
    fun onRemarksChange(rem: String) = _uiState.update { it.copy(additionalRemarks = rem) }

    fun updatePersonnelCount(count: Int) {
        val safeCount = count.coerceAtLeast(0)
        _uiState.update {
            val updated = it.copy(affectedPersonnel = safeCount)
            updated.copy(dynamicAiRisk = recomputeRisk(updated))
        }
    }

    fun addEvidencePhoto(uriString: String) {
        _uiState.update {
            it.copy(capturedEvidenceUris = it.capturedEvidenceUris + uriString)
        }
    }

    fun removeEvidence(index: Int) {
        _uiState.update {
            val list = it.capturedEvidenceUris.toMutableList()
            if (index in list.indices) list.removeAt(index)
            it.copy(capturedEvidenceUris = list)
        }
    }

    fun selectSector(sector: String) {
        _uiState.update {
            val updated = it.copy(mineSector = sector)
            updated.copy(dynamicAiRisk = recomputeRisk(updated))
        }
    }

    private fun recomputeRisk(state: EmergencyUiState): AiRiskAssessment {
        return AiRiskEngine.evaluateEmergencyRisk(
            type = state.selectedType,
            severity = state.selectedSeverity,
            affectedPersonnel = state.affectedPersonnel,
            sector = state.mineSector
        )
    }

    fun submitEmergencyAlert() {
        if (_uiState.value.isSubmitting) return
        _uiState.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            val state = _uiState.value
            val result = emergencyRepository.submitEmergencyReport(
                incidentType = state.selectedType,
                severity = state.selectedSeverity,
                title = state.title.ifBlank { "${state.selectedType.displayName} Incident" },
                description = state.description.ifBlank { "Emergency safety hazard reported at ${state.mineSector}" },
                immediateObservations = state.immediateObservations.ifBlank { "Immediate evacuation warning issued." },
                affectedPersonnel = state.affectedPersonnel,
                remarks = state.additionalRemarks,
                latitude = state.latitude,
                longitude = state.longitude,
                mineSector = state.mineSector,
                officerEmployeeId = state.reportingOfficerId,
                mediaUris = state.capturedEvidenceUris,
                currentNetworkMode = state.currentNetworkMode
            )

            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    submissionResult = result,
                    showSuccessDialog = true
                )
            }
        }
    }

    fun dismissSuccessDialog() {
        _uiState.update { it.copy(showSuccessDialog = false) }
    }
}
