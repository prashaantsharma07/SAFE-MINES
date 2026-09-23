package com.mine.governance

import android.app.Application
import com.mine.governance.data.local.MineDatabase
import com.mine.governance.data.repository.AuthRepository
import com.mine.governance.data.repository.EmergencyRepository
import com.mine.governance.data.repository.SyncRepository
import com.mine.governance.data.repository.TaskRepository
import com.mine.governance.data.sync.NetworkMonitor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class MineApplication : Application() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy {
        MineDatabase.getInstance(this, applicationScope)
    }

    val networkMonitor by lazy {
        NetworkMonitor(this)
    }

    val syncRepository by lazy {
        SyncRepository(
            syncQueueDao = database.syncQueueDao(),
            emergencyDao = database.emergencyDao(),
            taskDao = database.taskDao()
        )
    }

    val authRepository by lazy {
        AuthRepository(userDao = database.userDao())
    }

    val emergencyRepository by lazy {
        EmergencyRepository(
            emergencyDao = database.emergencyDao(),
            syncRepository = syncRepository
        )
    }

    val taskRepository by lazy {
        TaskRepository(
            taskDao = database.taskDao(),
            syncRepository = syncRepository
        )
    }

    override fun onCreate() {
        super.onCreate()
        // Pre-warm database instance
        database
    }
}
