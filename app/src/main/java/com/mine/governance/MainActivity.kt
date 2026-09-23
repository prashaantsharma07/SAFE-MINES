package com.mine.governance

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.mine.governance.ui.navigation.MineAppNavHost
import com.mine.governance.ui.screens.emergency.EmergencyViewModel
import com.mine.governance.ui.screens.home.HomeViewModel
import com.mine.governance.ui.screens.login.LoginViewModel
import com.mine.governance.ui.screens.tasks.TaskViewModel
import com.mine.governance.ui.theme.MineGovernanceTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val app = application as MineApplication

        val loginViewModel: LoginViewModel by viewModels {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return LoginViewModel(app.authRepository) as T
                }
            }
        }

        val homeViewModel: HomeViewModel by viewModels {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(
                        authRepository = app.authRepository,
                        taskRepository = app.taskRepository,
                        emergencyRepository = app.emergencyRepository,
                        syncRepository = app.syncRepository,
                        networkMonitor = app.networkMonitor
                    ) as T
                }
            }
        }

        val emergencyViewModel: EmergencyViewModel by viewModels {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return EmergencyViewModel(
                        emergencyRepository = app.emergencyRepository,
                        authRepository = app.authRepository,
                        networkMonitor = app.networkMonitor
                    ) as T
                }
            }
        }

        val taskViewModel: TaskViewModel by viewModels {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TaskViewModel(
                        taskRepository = app.taskRepository,
                        networkMonitor = app.networkMonitor
                    ) as T
                }
            }
        }

        setContent {
            MineGovernanceTheme {
                MineAppNavHost(
                    loginViewModel = loginViewModel,
                    homeViewModel = homeViewModel,
                    emergencyViewModel = emergencyViewModel,
                    taskViewModel = taskViewModel
                )
            }
        }
    }
}
