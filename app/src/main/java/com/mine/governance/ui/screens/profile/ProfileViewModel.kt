package com.mine.governance.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mine.governance.data.local.entity.UserEntity
import com.mine.governance.data.repository.AuthRepository
import com.mine.governance.data.repository.SyncRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ProfileUiState(
    val user: UserEntity? = null,
    val pendingSyncCount: Int = 0,
    val lastSyncTime: String = "10:32 AM",
    val isSyncing: Boolean = false,
    val isLoggedOut: Boolean = false
)

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val syncRepository: SyncRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.activeUserFlow.collect { activeUser ->
                _uiState.update { it.copy(user = activeUser) }
            }
        }

        viewModelScope.launch {
            syncRepository.pendingSyncCountFlow.collect { count ->
                _uiState.update { it.copy(pendingSyncCount = count) }
            }
        }
    }

    fun triggerSync() {
        if (_uiState.value.isSyncing) return
        _uiState.update { it.copy(isSyncing = true) }

        viewModelScope.launch {
            syncRepository.dispatchPendingQueue()
            val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
            _uiState.update {
                it.copy(
                    isSyncing = false,
                    lastSyncTime = timeFormatter.format(Date())
                )
            }
        }
    }

    fun logout(onLogoutComplete: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.update { it.copy(isLoggedOut = true) }
            onLogoutComplete()
        }
    }
}
