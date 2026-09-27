package com.mine.governance.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.mine.governance.ui.components.MineBottomBar
import com.mine.governance.ui.components.NavItem
import com.mine.governance.ui.screens.emergency.EmergencyReportScreen
import com.mine.governance.ui.screens.emergency.EmergencyViewModel
import com.mine.governance.ui.screens.home.HomeScreen
import com.mine.governance.ui.screens.home.HomeViewModel
import com.mine.governance.ui.screens.login.LoginScreen
import com.mine.governance.ui.screens.login.LoginViewModel
import com.mine.governance.ui.screens.map.MineMapScreen
import com.mine.governance.ui.screens.observations.ObservationScreen
import com.mine.governance.ui.screens.observations.ObservationViewModel
import com.mine.governance.ui.screens.profile.ProfileScreen
import com.mine.governance.ui.screens.profile.ProfileViewModel
import com.mine.governance.ui.screens.tasks.TaskViewModel
import com.mine.governance.ui.screens.tasks.TasksScreen
import com.mine.governance.ui.theme.DeepWarmStone

object MineDestinations {
    const val LOGIN = "login"
    const val HOME = "home"
    const val EMERGENCY = "emergency"
    const val TASKS = "tasks"
    const val REPORTS = "reports"
    const val MAP = "map"
    const val PROFILE = "profile"
}

@Composable
fun MineAppNavHost(
    navController: NavHostController = rememberNavController(),
    loginViewModel: LoginViewModel,
    homeViewModel: HomeViewModel,
    emergencyViewModel: EmergencyViewModel,
    taskViewModel: TaskViewModel,
    observationViewModel: ObservationViewModel,
    profileViewModel: ProfileViewModel
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: MineDestinations.LOGIN

    val showBottomBar = currentRoute in listOf(
        MineDestinations.HOME,
        MineDestinations.TASKS,
        MineDestinations.REPORTS,
        MineDestinations.MAP,
        MineDestinations.PROFILE
    )

    Scaffold(
        containerColor = DeepWarmStone,
        bottomBar = {
            if (showBottomBar) {
                MineBottomBar(
                    currentRoute = currentRoute,
                    onNavigate = { navItem ->
                        val targetRoute = when (navItem) {
                            NavItem.HOME -> MineDestinations.HOME
                            NavItem.TASKS -> MineDestinations.TASKS
                            NavItem.REPORTS -> MineDestinations.REPORTS
                            NavItem.MAP -> MineDestinations.MAP
                            NavItem.PROFILE -> MineDestinations.PROFILE
                        }
                        if (currentRoute != targetRoute) {
                            navController.navigate(targetRoute) {
                                popUpTo(MineDestinations.HOME) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = MineDestinations.LOGIN,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(MineDestinations.LOGIN) {
                LoginScreen(
                    viewModel = loginViewModel,
                    onLoginSuccess = {
                        navController.navigate(MineDestinations.HOME) {
                            popUpTo(MineDestinations.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            composable(MineDestinations.HOME) {
                HomeScreen(
                    viewModel = homeViewModel,
                    onNavigateToEmergency = { navController.navigate(MineDestinations.EMERGENCY) },
                    onNavigateToTasks = { navController.navigate(MineDestinations.TASKS) },
                    onNavigateToObservation = { navController.navigate(MineDestinations.REPORTS) },
                    onNavigateToInspection = { navController.navigate(MineDestinations.MAP) }
                )
            }

            composable(MineDestinations.EMERGENCY) {
                EmergencyReportScreen(
                    viewModel = emergencyViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(MineDestinations.TASKS) {
                TasksScreen(
                    viewModel = taskViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToEmergency = { navController.navigate(MineDestinations.EMERGENCY) }
                )
            }

            composable(MineDestinations.REPORTS) {
                ObservationScreen(
                    viewModel = observationViewModel,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(MineDestinations.MAP) {
                MineMapScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(MineDestinations.PROFILE) {
                ProfileScreen(
                    viewModel = profileViewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onLogoutSuccess = {
                        navController.navigate(MineDestinations.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
