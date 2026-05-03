package com.example.saludity

import android.os.Bundle
import android.content.Context
import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.saludity.ui.navigation.SaludityNavGraph
import com.example.saludity.ui.theme.SaludityTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val app = application as SaludityApp
            val themePreference by app.userSettings.themePreference.collectAsState(initial = "System")
            val languagePreference by app.userSettings.languagePreference.collectAsState(initial = "es")

            LaunchedEffect(languagePreference) {
                updateLocale(this@MainActivity, languagePreference)
            }

            val darkTheme = when (themePreference) {
                "Dark" -> true
                "Light" -> false
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            CompositionLocalProvider(androidx.compose.ui.platform.LocalContext provides this@MainActivity) {
                SaludityTheme(darkTheme = darkTheme, dynamicColor = false) {
                    val navController = rememberNavController()
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        SaludityNavGraph(
                            navController = navController,
                            app = app
                        )
                    }
                }
            }
        }
    }

    private fun updateLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
        
        // This is a legacy way to update locale at runtime. 
        // For modern Android, consider using AppCompatDelegate.setApplicationLocales()
        // but for a simple Compose app, updating context resources or restarting activity is common.
    }
}
