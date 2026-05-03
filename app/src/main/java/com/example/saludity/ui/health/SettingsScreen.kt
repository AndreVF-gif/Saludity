package com.example.saludity.ui.health

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.saludity.R
import com.example.saludity.data.datastore.UserSettings
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun SettingsScreen(
    userSettings: UserSettings,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val themePreference by userSettings.themePreference.collectAsState(initial = "System")
    val notificationsEnabled by userSettings.notificationsEnabled.collectAsState(initial = false)
    val languagePreference by userSettings.languagePreference.collectAsState(initial = "es")
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.settings_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back_button))
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(R.string.appearance_section), style = MaterialTheme.typography.titleMedium)
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.dark_mode))
                Switch(
                    checked = themePreference == "Dark",
                    onCheckedChange = { isChecked ->
                        coroutineScope.launch {
                            userSettings.saveThemePreference(if (isChecked) "Dark" else "Light")
                        }
                    }
                )
            }

            HorizontalDivider()

            Text(stringResource(R.string.notifications_section), style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.daily_reminder))
                
                val permissionState = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    rememberPermissionState(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    null
                }

                Switch(
                    checked = notificationsEnabled,
                    onCheckedChange = { isChecked ->
                        coroutineScope.launch {
                            if (isChecked && permissionState != null && !permissionState.status.isGranted) {
                                permissionState.launchPermissionRequest()
                            }
                            userSettings.saveNotificationsEnabled(isChecked)
                        }
                    }
                )
            }

            HorizontalDivider()

            Text(stringResource(R.string.language_section), style = MaterialTheme.typography.titleMedium)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.language_option))
                TextButton(
                    onClick = {
                        coroutineScope.launch {
                            userSettings.saveLanguagePreference(if (languagePreference == "es") "en" else "es")
                        }
                    }
                ) {
                    Text(if (languagePreference == "es") "Español" else "English")
                }
            }
        }
    }
}
