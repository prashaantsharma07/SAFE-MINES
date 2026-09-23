package com.mine.governance.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.mine.governance.ui.screens.tasks.TaskViewModel
import com.mine.governance.ui.screens.tasks.TasksScreen
import com.mine.governance.ui.theme.DarkGlassBackgroundBrush
import com.mine.governance.ui.theme.ImperialGold
import com.mine.governance.ui.theme.ObsidianBlack
import com.mine.governance.ui.theme.TextSecondary

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
    taskViewModel: TaskViewModel
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
        containerColor = ObsidianBlack,
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
                    onNavigateToInspection = { navController.navigate(MineDestinations.TASKS) }
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
                PlaceholderGlassScreen(
                    title = "SAFETY OBSERVATIONS & REPORTS",
                    subtitle = "Report non-emergency safety violations, dust, ventilation, and contractor hazards.",
                    onBack = { navController.popBackStack() }
                )
            }

            composable(MineDestinations.MAP) {
                PlaceholderGlassScreen(
                    title = "GIS SUBTERRANEAN MINE MAP",
                    subtitle = "Interactive 3D drift model with hazard clusters and active miner beacons.",
                    onBack = { navController.popBackStack() }
                )
            }

            composable(MineDestinations.PROFILE) {
                PlaceholderGlassScreen(
                    title = "OFFICER PROFILE & SETTINGS",
                    subtitle = "Officer Rajesh Kumar • Safety Field Operations • Level 3 Compliance Authority.",
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun PlaceholderGlassScreen(
    title: String,
    subtitle: String,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGlassBackgroundBrush)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = ImperialGold,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = subtitle,
                fontSize = 13.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}
