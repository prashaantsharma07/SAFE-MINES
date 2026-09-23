package com.mine.governance.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mine.governance.data.local.entity.UserEntity
import com.mine.governance.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val employeeId: String = "EMP-7842",
    val password: String = "••••••••",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
    val currentUser: UserEntity? = null
)

class LoginViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            authRepository.activeUserFlow.collect { user ->
                _uiState.update { it.copy(currentUser = user) }
            }
        }
    }

    fun onEmployeeIdChange(id: String) {
        _uiState.update { it.copy(employeeId = id, error = null) }
    }

    fun onPasswordChange(pwd: String) {
        _uiState.update { it.copy(password = pwd, error = null) }
    }

    fun login(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.employeeId.isBlank() || state.password.isBlank()) {
            _uiState.update { it.copy(error = "Please enter Employee ID and Password") }
            return
        }

        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = authRepository.login(state.employeeId, state.password)
            result.onSuccess { user ->
                _uiState.update { it.copy(isLoading = false, isSuccess = true, currentUser = user) }
                onSuccess()
            }.onFailure { err ->
                _uiState.update { it.copy(isLoading = false, error = err.message ?: "Authentication failed") }
            }
        }
    }

    fun fillDemoOfficer() {
        _uiState.update {
            it.copy(
                employeeId = "EMP-7842",
                password = "MineSafety@2026",
                error = null
            )
        }
    }
}
