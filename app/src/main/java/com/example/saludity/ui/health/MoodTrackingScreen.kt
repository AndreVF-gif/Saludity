package com.example.saludity.ui.health

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.SentimentDissatisfied
import androidx.compose.material.icons.rounded.SentimentNeutral
import androidx.compose.material.icons.rounded.SentimentSatisfied
import androidx.compose.material.icons.rounded.SentimentVeryDissatisfied
import androidx.compose.material.icons.rounded.SentimentVerySatisfied
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.saludity.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodTrackingScreen(
    viewModel: HealthViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val moodState by viewModel.moodState.collectAsState()
    var selectedMood by remember { mutableIntStateOf(moodState?.moodScore ?: 3) }
    var note by remember { mutableStateOf(moodState?.note ?: "") }

    // Navigation protection logic
    var lastClickTime by remember { mutableLongStateOf(0L) }
    val safeAction: (() -> Unit) -> Unit = { action ->
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime > 600L) {
            lastClickTime = currentTime
            action()
        }
    }

    // Update local state when moodState changes from database
    LaunchedEffect(moodState) {
        moodState?.let {
            selectedMood = it.moodScore
            note = it.note ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.mindfulness_mood)) },
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
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = stringResource(R.string.mindfulness_subtitle),
                style = MaterialTheme.typography.headlineSmall
            )

            MoodSelector(
                selectedMood = selectedMood,
                onMoodSelected = { selectedMood = it }
            )

            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(stringResource(R.string.mood_note_label)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Button(
                onClick = {
                    safeAction {
                        viewModel.updateMood(selectedMood, note.takeIf { it.isNotBlank() })
                        onNavigateBack()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save_checkin))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.4f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = stringResource(R.string.mindfulness_tip_title),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.mindfulness_tip_content),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun MoodSelector(
    selectedMood: Int,
    onMoodSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        MoodIcon(1, Icons.Rounded.SentimentVeryDissatisfied, stringResource(R.string.mood_awful), selectedMood == 1) { onMoodSelected(1) }
        MoodIcon(2, Icons.Rounded.SentimentDissatisfied, stringResource(R.string.mood_bad), selectedMood == 2) { onMoodSelected(2) }
        MoodIcon(3, Icons.Rounded.SentimentNeutral, stringResource(R.string.mood_ok), selectedMood == 3) { onMoodSelected(3) }
        MoodIcon(4, Icons.Rounded.SentimentSatisfied, stringResource(R.string.mood_good), selectedMood == 4) { onMoodSelected(4) }
        MoodIcon(5, Icons.Rounded.SentimentVerySatisfied, stringResource(R.string.mood_great), selectedMood == 5) { onMoodSelected(5) }
    }
}

@Composable
fun MoodIcon(
    score: Int,
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(40.dp)
            )
        }
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
        )
    }
}
