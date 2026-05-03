package com.example.saludity.ui.health

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.saludity.R
import com.example.saludity.ui.components.ActionCard
import com.example.saludity.ui.components.ProgressGauge
import com.example.saludity.ui.theme.CalorieYellow
import com.example.saludity.ui.theme.MinutesRed
import com.example.saludity.ui.theme.StepsLime
import com.example.saludity.ui.theme.WaterTurquoise
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: HealthViewModel,
    onNavigateToNutrition: () -> Unit,
    onNavigateToMood: () -> Unit,
    onNavigateToInsights: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val userName by viewModel.userName.collectAsState()
    val activityState by viewModel.activityState.collectAsState()
    val nutritionState by viewModel.nutritionState.collectAsState()
    val moodState by viewModel.moodState.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Navigation protection logic
    var lastClickTime by remember { mutableLongStateOf(0L) }
    val safeNavigate: (() -> Unit) -> Unit = { action ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > 600L) { // 600ms debounce
            lastClickTime = currentTime
            action()
        }
    }

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    var showStepsDialog by remember { mutableStateOf(false) }
    var stepsInput by remember { mutableStateOf("") }
    var showMinutesDialog by remember { mutableStateOf(false) }
    var minutesInput by remember { mutableStateOf("") }

    if (showStepsDialog) {
        AlertDialog(
            onDismissRequest = { showStepsDialog = false },
            title = { Text(stringResource(R.string.add_steps)) },
            text = {
                OutlinedTextField(
                    value = stepsInput,
                    onValueChange = { stepsInput = it.filter { char -> char.isDigit() } },
                    label = { Text(stringResource(R.string.steps_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        stepsInput.toIntOrNull()?.let { viewModel.addSteps(it) }
                        showStepsDialog = false
                        stepsInput = ""
                    }
                ) {
                    Text(stringResource(R.string.add_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { showStepsDialog = false }) {
                    Text(stringResource(R.string.cancel_button))
                }
            }
        )
    }

    if (showMinutesDialog) {
        AlertDialog(
            onDismissRequest = { showMinutesDialog = false },
            title = { Text(stringResource(R.string.add_minutes)) },
            text = {
                OutlinedTextField(
                    value = minutesInput,
                    onValueChange = { minutesInput = it.filter { char -> char.isDigit() } },
                    label = { Text(stringResource(R.string.minutes_label)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        minutesInput.toIntOrNull()?.let { viewModel.addActiveMinutes(it) }
                        showMinutesDialog = false
                        minutesInput = ""
                    }
                ) {
                    Text(stringResource(R.string.add_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { showMinutesDialog = false }) {
                    Text(stringResource(R.string.cancel_button))
                }
            }
        )
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                NavigationDrawerItem(
                    icon = { Icon(Icons.Rounded.History, contentDescription = null) },
                    label = { Text(stringResource(R.string.history_title)) },
                    selected = false,
                    onClick = {
                        safeNavigate {
                            scope.launch {
                                drawerState.close()
                                delay(100) // Small cushion for animation stability
                                onNavigateToInsights()
                            }
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.Rounded.Settings, contentDescription = null) },
                    label = { Text(stringResource(R.string.settings_title)) },
                    selected = false,
                    onClick = {
                        safeNavigate {
                            scope.launch {
                                drawerState.close()
                                delay(100)
                                onNavigateToSettings()
                            }
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
                NavigationDrawerItem(
                    icon = { Icon(Icons.AutoMirrored.Rounded.Logout, contentDescription = null) },
                    label = { Text(stringResource(R.string.logout_title)) },
                    selected = false,
                    onClick = {
                        safeNavigate {
                            scope.launch {
                                drawerState.close()
                                onLogout()
                            }
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { 
                        Column {
                            Text(stringResource(R.string.dashboard_title), style = MaterialTheme.typography.titleLarge)
                            userName?.let {
                                Text(
                                    stringResource(R.string.welcome_back, it),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { 
                            if (drawerState.isClosed) {
                                scope.launch { drawerState.open() }
                            }
                        }) {
                            Icon(Icons.Rounded.Menu, contentDescription = "Menu")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            modifier = modifier.fillMaxSize()
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    Text(
                        text = stringResource(R.string.nutrition_hydration_headline),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProgressGauge(
                            progress = (nutritionState?.calories ?: 0).toFloat() / viewModel.calorieGoal,
                            label = stringResource(R.string.calories),
                            currentValue = (nutritionState?.calories ?: 0).toString(),
                            goalValue = viewModel.calorieGoal.toString(),
                            color = CalorieYellow
                        )
                        ProgressGauge(
                            progress = (nutritionState?.waterMl ?: 0).toFloat() / viewModel.waterGoalMl,
                            label = stringResource(R.string.water),
                            currentValue = (nutritionState?.waterMl ?: 0).toString(),
                            goalValue = "${viewModel.waterGoalMl}ml",
                            color = WaterTurquoise
                        )
                    }
                }

                item {
                    Text(
                        text = stringResource(R.string.daily_activity_headline),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProgressGauge(
                            progress = (activityState?.steps ?: 0).toFloat() / viewModel.stepGoal,
                            label = stringResource(R.string.steps),
                            currentValue = (activityState?.steps ?: 0).toString(),
                            goalValue = viewModel.stepGoal.toString(),
                            color = StepsLime
                        )
                        ProgressGauge(
                            progress = (activityState?.activeMinutes ?: 0).toFloat() / viewModel.activeMinutesGoal,
                            label = stringResource(R.string.active_minutes_label),
                            currentValue = (activityState?.activeMinutes ?: 0).toString(),
                            goalValue = viewModel.activeMinutesGoal.toString(),
                            color = MinutesRed
                        )
                    }
                }

                item {
                    Text(
                        text = stringResource(R.string.health_insights_headline),
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    Card(
                        onClick = onNavigateToInsights,
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Favorite,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                val moodText = when (moodState?.moodScore) {
                                    1 -> stringResource(R.string.mood_awful)
                                    2 -> stringResource(R.string.mood_bad)
                                    3 -> stringResource(R.string.mood_ok)
                                    4 -> stringResource(R.string.mood_good)
                                    5 -> stringResource(R.string.mood_great)
                                    else -> stringResource(R.string.mood_not_logged)
                                }
                                Text(
                                    text = stringResource(R.string.mood_prefix, moodText),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                if (moodState?.note != null) {
                                    Text(
                                        text = moodState?.note ?: "",
                                        style = MaterialTheme.typography.bodySmall,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = stringResource(R.string.log_progress_header),
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    ActionCard(
                        title = stringResource(R.string.log_nutrition),
                        subtitle = stringResource(R.string.log_nutrition_subtitle),
                        icon = { Icon(Icons.Rounded.Restaurant, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary) },
                        onClick = { safeNavigate { onNavigateToNutrition() } }
                    )
                    ActionCard(
                        title = stringResource(R.string.add_steps),
                        subtitle = stringResource(R.string.add_steps_subtitle),
                        icon = { Icon(Icons.AutoMirrored.Rounded.DirectionsRun, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        onClick = { showStepsDialog = true }
                    )
                    ActionCard(
                        title = stringResource(R.string.add_minutes),
                        subtitle = stringResource(R.string.add_minutes_subtitle),
                        icon = { Icon(Icons.Rounded.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.secondary) },
                        onClick = { showMinutesDialog = true }
                    )
                    ActionCard(
                        title = stringResource(R.string.mindfulness_mood),
                        subtitle = stringResource(R.string.mindfulness_subtitle),
                        icon = { Icon(Icons.Rounded.SelfImprovement, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                        onClick = { safeNavigate { onNavigateToMood() } }
                    )
                }
                
                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
