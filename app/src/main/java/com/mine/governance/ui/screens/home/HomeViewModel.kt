package com.mine.governance.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mine.governance.data.local.entity.UserEntity
import com.mine.governance.data.repository.AuthRepository
import com.mine.governance.data.repository.EmergencyRepository
import com.mine.governance.data.repository.SyncRepository
import com.mine.governance.data.repository.TaskRepository
import com.mine.governance.data.sync.NetworkMonitor
import com.mine.governance.domain.model.NetworkMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HomeUiState(
    val currentUser: UserEntity? = null,
    val networkMode: NetworkMode = NetworkMode.OFFLINE,
    val pendingSyncCount: Int = 0,
    val lastSyncTime: String = "10:32 AM",
    val isSyncing: Boolean = false,
    val activeTasksCount: Int = 5,
    val criticalTasksCount: Int = 2,
    val totalReportsCount: Int = 14,
    val openIssuesCount: Int = 3,
    val completedInspectionsCount: Int = 8,
    val pendingInspectionsCount: Int = 2,
    val shiftTimestamp: String = ""
)

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val taskRepository: TaskRepository,
    private val emergencyRepository: EmergencyRepository,
    private val syncRepository: SyncRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        updateCurrentTime()
        observeData()
    }

    private fun updateCurrentTime() {
        val formatter = SimpleDateFormat("EEE, dd MMM • hh:mm a", Locale.getDefault())
        _uiState.update { it.copy(shiftTimestamp = formatter.format(Date())) }
    }

    private fun observeData() {
        viewModelScope.launch {
            authRepository.activeUserFlow.collect { user ->
                _uiState.update { it.copy(currentUser = user) }
            }
        }

        viewModelScope.launch {
            networkMonitor.networkModeFlow.collect { mode ->
                _uiState.update { it.copy(networkMode = mode) }
            }
        }

        viewModelScope.launch {
            syncRepository.pendingSyncCountFlow.collect { count ->
                _uiState.update { it.copy(pendingSyncCount = count) }
            }
        }

        viewModelScope.launch {
            taskRepository.activeTaskCountFlow.collect { active ->
                _uiState.update { it.copy(activeTasksCount = active) }
            }
        }

        viewModelScope.launch {
            taskRepository.criticalTaskCountFlow.collect { critical ->
                _uiState.update { it.copy(criticalTasksCount = critical) }
            }
        }

        viewModelScope.launch {
            emergencyRepository.allEmergenciesFlow.collect { list ->
                _uiState.update { it.copy(totalReportsCount = 10 + list.size) }
            }
        }
    }

    fun triggerSync() {
        if (_uiState.value.isSyncing) return
        _uiState.update { it.copy(isSyncing = true) }

        viewModelScope.launch {
            val result = syncRepository.dispatchPendingQueue(
                isMeshGatewayOnly = _uiState.value.networkMode == NetworkMode.WEAK_NETWORK
            )
            val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
            _uiState.update {
                it.copy(
                    isSyncing = false,
                    lastSyncTime = timeFormatter.format(Date())
                )
            }
        }
    }

    fun toggleNetworkMode() {
        networkMonitor.toggleNextMode(_uiState.value.networkMode)
    }
}
