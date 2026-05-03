package com.example.saludity.ui.health

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.LocalDrink
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.saludity.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NutritionLoggingScreen(
    viewModel: HealthViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var caloriesInput by remember { mutableStateOf("") }
    var waterInput by remember { mutableStateOf("") }

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
                title = { Text(stringResource(R.string.nutrition_hydration_title)) },
                navigationIcon = {
                    IconButton(onClick = { safeAction { onNavigateBack() } }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = stringResource(R.string.back_button))
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
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Calorie Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Restaurant, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.calories), style = MaterialTheme.typography.titleLarge)
                    }
                    OutlinedTextField(
                        value = caloriesInput,
                        onValueChange = { if (it.all { char -> char.isDigit() }) caloriesInput = it },
                        label = { Text(stringResource(R.string.enter_calories)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            caloriesInput.toIntOrNull()?.let {
                                viewModel.addCalories(it)
                                caloriesInput = ""
                            }
                        },
                        modifier = Modifier.align(Alignment.End),
                        enabled = caloriesInput.isNotBlank()
                    ) {
                        Text(stringResource(R.string.add_calories_button))
                    }
                }
            }

            // Water Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.LocalDrink, contentDescription = null, tint = MaterialTheme.colorScheme.secondary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = stringResource(R.string.hydration_section), style = MaterialTheme.typography.titleLarge)
                    }
                    OutlinedTextField(
                        value = waterInput,
                        onValueChange = { if (it.all { char -> char.isDigit() }) waterInput = it },
                        label = { Text(stringResource(R.string.enter_water)) },
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AssistChip(
                            onClick = { viewModel.addWater(250) },
                            label = { Text("250ml") }
                        )
                        AssistChip(
                            onClick = { viewModel.addWater(500) },
                            label = { Text("500ml") }
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Button(
                            onClick = {
                                waterInput.toIntOrNull()?.let {
                                    viewModel.addWater(it)
                                    waterInput = ""
                                }
                            },
                            enabled = waterInput.isNotBlank()
                        ) {
                            Text(stringResource(R.string.add_button))
                        }
                    }
                }
            }
        }
    }
}
