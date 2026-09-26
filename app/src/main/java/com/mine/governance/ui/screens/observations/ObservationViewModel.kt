package com.mine.governance.ui.screens.observations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mine.governance.data.gemini.GeminiSafetyAnalyzer
import com.mine.governance.data.gemini.SafetyAiAnalysis
import com.mine.governance.data.repository.AuthRepository
import com.mine.governance.data.repository.SyncRepository
import com.mine.governance.domain.model.Severity
import com.mine.governance.domain.model.SyncPriorityLevels
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class ObservationUiState(
    val title: String = "",
    val description: String = "",
    val sector: String = "Sector 3 • East Drift Haulage",
    val attachedPhotos: List<String> = emptyList(),
    val isAnalyzing: Boolean = false,
    val isSubmitting: Boolean = false,
    val aiAnalysis: SafetyAiAnalysis? = null,
    val submissionSuccessMessage: String? = null,
    val reportingOfficerName: String = "Rajesh Kumar"
)

class ObservationViewModel(
    private val safetyAnalyzer: GeminiSafetyAnalyzer = GeminiSafetyAnalyzer(),
    private val authRepository: AuthRepository,
    private val syncRepository: SyncRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ObservationUiState())
    val uiState: StateFlow<ObservationUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.activeUserFlow.collect { user ->
                if (user != null) {
                    _uiState.update { it.copy(reportingOfficerName = user.name) }
                }
            }
        }
    }

    fun onTitleChange(title: String) = _uiState.update { it.copy(title = title) }
    fun onDescriptionChange(desc: String) = _uiState.update { it.copy(description = desc) }
    fun onSectorChange(sec: String) = _uiState.update { it.copy(sector = sec) }

    fun addPhoto(uriString: String) {
        _uiState.update { it.copy(attachedPhotos = it.attachedPhotos + uriString) }
    }

    fun removePhoto(index: Int) {
        val list = _uiState.value.attachedPhotos.toMutableList()
        if (index in list.indices) list.removeAt(index)
        _uiState.update { it.copy(attachedPhotos = list) }
    }

    fun analyzeWithGemini() {
        val text = _uiState.value.description
        if (text.isBlank()) return

        _uiState.update { it.copy(isAnalyzing = true) }

        viewModelScope.launch {
            val analysis = safetyAnalyzer.analyzeObservation(text, _uiState.value.sector)
            _uiState.update {
                it.copy(
                    isAnalyzing = false,
                    aiAnalysis = analysis
                )
            }
        }
    }

    fun submitObservation() {
        val state = _uiState.value
        if (state.description.isBlank()) return

        _uiState.update { it.copy(isSubmitting = true) }

        viewModelScope.launch {
            val obsId = "OBS-" + UUID.randomUUID().toString().take(6).uppercase()
            val payload = """
                {"id":"$obsId","title":"${state.title}","sector":"${state.sector}","category":"${state.aiAnalysis?.category ?: "General"}","severity":"${state.aiAnalysis?.severity?.name ?: "MEDIUM"}"}
            """.trimIndent()

            syncRepository.enqueueItem(
                entityType = "OBSERVATION",
                entityId = obsId,
                payloadJson = payload,
                priority = SyncPriorityLevels.OBSERVATION
            )

            _uiState.update {
                it.copy(
                    isSubmitting = false,
                    submissionSuccessMessage = "Safety observation logged successfully ($obsId). Enqueued in Priority 3 Outbox queue."
                )
            }
        }
    }

    fun clearSuccessMessage() {
        _uiState.update {
            it.copy(
                submissionSuccessMessage = null,
                title = "",
                description = "",
                attachedPhotos = emptyList(),
                aiAnalysis = null
            )
        }
    }
}
