package com.mine.governance.ui.screens.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mine.governance.data.local.entity.TaskEntity
import com.mine.governance.data.repository.TaskRepository
import com.mine.governance.data.sync.NetworkMonitor
import com.mine.governance.domain.model.NetworkMode
import com.mine.governance.domain.model.Severity
import com.mine.governance.domain.model.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class TaskFilterTab(val displayName: String) {
    ALL("All"),
    CRITICAL("Critical"),
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    AWAITING_VERIFICATION("Verification"),
    OVERDUE("Overdue")
}

data class TaskUiState(
    val tasks: List<TaskEntity> = emptyList(),
    val filteredTasks: List<TaskEntity> = emptyList(),
    val selectedTab: TaskFilterTab = TaskFilterTab.ALL,
    val selectedTask: TaskEntity? = null,
    val isExecutingAction: Boolean = false,
    val networkMode: NetworkMode = NetworkMode.OFFLINE,
    val afterEvidenceUris: List<String> = emptyList(),
    val officerComments: String = ""
)

class TaskViewModel(
    private val taskRepository: TaskRepository,
    private val networkMonitor: NetworkMonitor
) : ViewModel() {

    private val _uiState = MutableStateFlow(TaskUiState())
    val uiState: StateFlow<TaskUiState> = _uiState.asStateFlow()

    init {
        observeTasks()
    }

    private fun observeTasks() {
        viewModelScope.launch {
            networkMonitor.networkModeFlow.collect { mode ->
                _uiState.update { it.copy(networkMode = mode) }
            }
        }

        viewModelScope.launch {
            taskRepository.allTasksFlow.collect { list ->
                _uiState.update {
                    it.copy(
                        tasks = list,
                        filteredTasks = applyFilter(list, it.selectedTab)
                    )
                }
            }
        }
    }

    fun selectTab(tab: TaskFilterTab) {
        _uiState.update {
            it.copy(
                selectedTab = tab,
                filteredTasks = applyFilter(it.tasks, tab)
            )
        }
    }

    private fun applyFilter(tasks: List<TaskEntity>, tab: TaskFilterTab): List<TaskEntity> {
        val now = System.currentTimeMillis()
        return when (tab) {
            TaskFilterTab.ALL -> tasks
            TaskFilterTab.CRITICAL -> tasks.filter { it.priority == Severity.CRITICAL }
            TaskFilterTab.PENDING -> tasks.filter { it.status == TaskStatus.PENDING }
            TaskFilterTab.IN_PROGRESS -> tasks.filter { it.status == TaskStatus.IN_PROGRESS }
            TaskFilterTab.AWAITING_VERIFICATION -> tasks.filter { it.status == TaskStatus.AWAITING_VERIFICATION }
            TaskFilterTab.OVERDUE -> tasks.filter { it.deadlineDate < now && it.status != TaskStatus.CLOSED }
        }
    }

    fun selectTask(task: TaskEntity?) {
        _uiState.update {
            it.copy(
                selectedTask = task,
                afterEvidenceUris = task?.afterMediaUris ?: emptyList(),
                officerComments = task?.officerComments ?: ""
            )
        }
    }

    fun startTask(taskId: String) {
        viewModelScope.launch {
            taskRepository.startTask(taskId, _uiState.value.networkMode)
            val updated = taskRepository.getTaskById(taskId)
            _uiState.update { it.copy(selectedTask = updated) }
        }
    }

    fun onCommentsChange(comments: String) {
        _uiState.update { it.copy(officerComments = comments) }
    }

    fun attachAfterEvidence() {
        val count = _uiState.value.afterEvidenceUris.size + 1
        val newUris = _uiState.value.afterEvidenceUris + "mock://evidence/resolved_work_$count.jpg"
        _uiState.update { it.copy(afterEvidenceUris = newUris) }
    }

    fun completeTask(taskId: String) {
        viewModelScope.launch {
            taskRepository.completeTask(
                taskId = taskId,
                afterEvidenceUris = _uiState.value.afterEvidenceUris,
                comments = _uiState.value.officerComments.ifBlank { "Corrective maintenance verified and operational." },
                networkMode = _uiState.value.networkMode
            )
            val updated = taskRepository.getTaskById(taskId)
            _uiState.update { it.copy(selectedTask = updated) }
        }
    }
}
