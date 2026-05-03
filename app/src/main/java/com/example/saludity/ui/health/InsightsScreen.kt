package com.example.saludity.ui.health

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.ShowChart
import androidx.compose.material.icons.rounded.CloudDone
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.CloudDownload
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.saludity.data.local.entity.ActivityEntity
import com.example.saludity.data.local.entity.MoodEntity
import com.example.saludity.data.local.entity.NutritionEntity
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InsightsScreen(
    viewModel: HealthViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val allActivities by viewModel.allActivities.collectAsState()
    val allNutrition by viewModel.allNutrition.collectAsState()
    val allMoods by viewModel.allMoods.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Activity", "Nutrition", "Mood")

    var showEditDialog by remember { mutableStateOf<Any?>(null) }

    // Navigation protection logic
    var lastClickTime by remember { mutableLongStateOf(0L) }
    val safeAction: (() -> Unit) -> Unit = { action ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > 600L) {
            lastClickTime = currentTime
            action()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Unified Health Insights") },
                navigationIcon = {
                    IconButton(onClick = { safeAction { onNavigateBack() } }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { safeAction { viewModel.restoreData() } }) {
                        Icon(Icons.Rounded.CloudDownload, contentDescription = "Restore from Cloud")
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            PrimaryTabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title) }
                    )
                }
            }

            when (selectedTab) {
                0 -> ActivityList(allActivities, onDelete = { viewModel.deleteActivity(it) }, onEdit = { showEditDialog = it })
                1 -> NutritionList(allNutrition, onDelete = { viewModel.deleteNutrition(it) }, onEdit = { showEditDialog = it })
                2 -> MoodList(allMoods, onDelete = { viewModel.deleteMood(it) }, onEdit = { showEditDialog = it })
            }
        }

        if (showEditDialog != null) {
            EditDialog(
                item = showEditDialog!!,
                onDismiss = { showEditDialog = null },
                onSave = { updatedItem ->
                    when (updatedItem) {
                        is ActivityEntity -> viewModel.updateActivity(updatedItem)
                        is NutritionEntity -> viewModel.updateNutrition(updatedItem)
                        is MoodEntity -> viewModel.updateMood(updatedItem)
                    }
                    showEditDialog = null
                }
            )
        }
    }
}

@Composable
fun ActivityList(activities: List<ActivityEntity>, onDelete: (ActivityEntity) -> Unit, onEdit: (ActivityEntity) -> Unit) {
    if (activities.isEmpty()) {
        EmptyState("No activity data available yet.")
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(activities) { activity ->
                TrendItem(
                    title = activity.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                    subtitle = "${activity.steps} steps • ${activity.activeMinutes} active minutes",
                    icon = Icons.AutoMirrored.Rounded.ShowChart,
                    isSynced = activity.isSynced,
                    onDelete = { onDelete(activity) },
                    onEdit = { onEdit(activity) }
                )
            }
        }
    }
}

@Composable
fun NutritionList(nutritionList: List<NutritionEntity>, onDelete: (NutritionEntity) -> Unit, onEdit: (NutritionEntity) -> Unit) {
    if (nutritionList.isEmpty()) {
        EmptyState("No nutrition data available yet.")
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(nutritionList) { nutrition ->
                TrendItem(
                    title = nutrition.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                    subtitle = "${nutrition.calories} kcal • ${nutrition.waterMl}ml water",
                    icon = Icons.Rounded.Restaurant,
                    isSynced = nutrition.isSynced,
                    onDelete = { onDelete(nutrition) },
                    onEdit = { onEdit(nutrition) }
                )
            }
        }
    }
}

@Composable
fun MoodList(moods: List<MoodEntity>, onDelete: (MoodEntity) -> Unit, onEdit: (MoodEntity) -> Unit) {
    if (moods.isEmpty()) {
        EmptyState("No mood data available yet.")
    } else {
        LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(moods) { mood ->
                val moodText = when (mood.moodScore) {
                    1 -> "Awful"
                    2 -> "Bad"
                    3 -> "OK"
                    4 -> "Good"
                    5 -> "Great"
                    else -> "Unknown"
                }
                TrendItem(
                    title = mood.date.format(DateTimeFormatter.ofPattern("MMM dd, yyyy")),
                    subtitle = "Mood: $moodText${if (mood.note != null) " • ${mood.note}" else ""}",
                    icon = Icons.Rounded.Favorite,
                    isSynced = mood.isSynced,
                    onDelete = { onDelete(mood) },
                    onEdit = { onEdit(mood) }
                )
            }
        }
    }
}

@Composable
fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun TrendItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSynced: Boolean,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1.0f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = if (isSynced) Icons.Rounded.CloudDone else Icons.Rounded.CloudOff,
                        contentDescription = if (isSynced) "Synced" else "Not synced",
                        tint = if (isSynced) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Rounded.Edit,
                    contentDescription = "Edit record",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
                    contentDescription = "Delete record",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun EditDialog(
    item: Any,
    onDismiss: () -> Unit,
    onSave: (Any) -> Unit
) {
    var field1 by remember { mutableStateOf("") }
    var field2 by remember { mutableStateOf("") }

    val title = when (item) {
        is ActivityEntity -> "Edit Activity"
        is NutritionEntity -> "Edit Nutrition"
        is MoodEntity -> "Edit Mood"
        else -> ""
    }

    LaunchedEffect(item) {
        when (item) {
            is ActivityEntity -> {
                field1 = item.steps.toString()
                field2 = item.activeMinutes.toString()
            }
            is NutritionEntity -> {
                field1 = item.calories.toString()
                field2 = item.waterMl.toString()
            }
            is MoodEntity -> {
                field1 = item.moodScore.toString()
                field2 = item.note ?: ""
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                when (item) {
                    is ActivityEntity -> {
                        OutlinedTextField(
                            value = field1,
                            onValueChange = { if (it.all { c -> c.isDigit() }) field1 = it },
                            label = { Text("Steps") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = field2,
                            onValueChange = { if (it.all { c -> c.isDigit() }) field2 = it },
                            label = { Text("Active Minutes") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    is NutritionEntity -> {
                        OutlinedTextField(
                            value = field1,
                            onValueChange = { if (it.all { c -> c.isDigit() }) field1 = it },
                            label = { Text("Calories") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = field2,
                            onValueChange = { if (it.all { c -> c.isDigit() }) field2 = it },
                            label = { Text("Water (ml)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                    }
                    is MoodEntity -> {
                        Text("Mood Score (1-5)")
                        Slider(
                            value = field1.toFloatOrNull() ?: 3f,
                            onValueChange = { field1 = it.toInt().toString() },
                            valueRange = 1f..5f,
                            steps = 3
                        )
                        OutlinedTextField(
                            value = field2,
                            onValueChange = { field2 = it },
                            label = { Text("Note") }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val updatedItem = when (item) {
                        is ActivityEntity -> item.copy(steps = field1.toIntOrNull() ?: item.steps, activeMinutes = field2.toIntOrNull() ?: item.activeMinutes)
                        is NutritionEntity -> item.copy(calories = field1.toIntOrNull() ?: item.calories, waterMl = field2.toIntOrNull() ?: item.waterMl)
                        is MoodEntity -> item.copy(moodScore = field1.toIntOrNull() ?: item.moodScore, note = field2.takeIf { it.isNotBlank() })
                        else -> item
                    }
                    onSave(updatedItem)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
