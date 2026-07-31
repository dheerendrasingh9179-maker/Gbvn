package com.example.ui

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FocusNotesScreen
import com.example.ui.screens.HabitsScreen
import com.example.ui.screens.TasksScreen
import com.example.ui.theme.IndigoPrimary

sealed class NavigationTab(
    val route: String,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
) {
    object Dashboard : NavigationTab("dashboard", "Today", Icons.Filled.Dashboard, Icons.Outlined.Dashboard, "tab_dashboard")
    object Tasks : NavigationTab("tasks", "Tasks", Icons.Filled.CheckCircle, Icons.Outlined.CheckCircle, "tab_tasks")
    object Habits : NavigationTab("habits", "Habits", Icons.Filled.LocalFireDepartment, Icons.Outlined.LocalFireDepartment, "tab_habits")
    object Focus : NavigationTab("focus", "Focus", Icons.Filled.Timer, Icons.Outlined.Timer, "tab_focus")
}

@Composable
fun MainScreen(viewModel: PlannerViewModel) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf(
        NavigationTab.Dashboard,
        NavigationTab.Tasks,
        NavigationTab.Habits,
        NavigationTab.Focus
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                windowInsets = WindowInsets.navigationBars
            ) {
                tabs.forEachIndexed { index, tab ->
                    val isSelected = selectedTabIndex == index
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { selectedTabIndex = index },
                        icon = {
                            Icon(
                                imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                                contentDescription = tab.title
                            )
                        },
                        label = { Text(tab.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndigoPrimary,
                            selectedTextColor = IndigoPrimary
                        ),
                        modifier = Modifier.testTag(tab.testTag)
                    )
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)
        when (selectedTabIndex) {
            0 -> DashboardScreen(
                viewModel = viewModel,
                onNavigateToTasks = { selectedTabIndex = 1 },
                onNavigateToHabits = { selectedTabIndex = 2 },
                onNavigateToFocus = { selectedTabIndex = 3 },
                modifier = screenModifier
            )
            1 -> TasksScreen(viewModel = viewModel, modifier = screenModifier)
            2 -> HabitsScreen(viewModel = viewModel, modifier = screenModifier)
            3 -> FocusNotesScreen(viewModel = viewModel, modifier = screenModifier)
        }
    }
}
