package com.example.saludity

import android.app.Application
import androidx.room.Room
import androidx.work.*
import com.example.saludity.data.datastore.UserSettings
import com.example.saludity.data.local.SaludityDatabase
import com.example.saludity.data.repository.FirebaseRepository
import com.example.saludity.data.repository.HealthRepository
import com.example.saludity.data.sync.SyncWorker
import com.example.saludity.data.sync.ReminderWorker
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.concurrent.TimeUnit

class SaludityApp : Application() {
    private val database by lazy {
        Room.databaseBuilder(
            this,
            SaludityDatabase::class.java,
            SaludityDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    private val firebaseRepository by lazy {
        FirebaseRepository(FirebaseAuth.getInstance(), FirebaseFirestore.getInstance())
    }

    val repository by lazy {
        HealthRepository(database.healthDao(), firebaseRepository)
    }

    val userSettings by lazy {
        UserSettings(this)
    }

    override fun onCreate() {
        super.onCreate()
        scheduleSync()
        scheduleReminder()
    }

    private fun scheduleSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "HealthDataSync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
    }

    private fun scheduleReminder() {
        val reminderRequest = PeriodicWorkRequestBuilder<ReminderWorker>(1, TimeUnit.DAYS)
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "DailyReminder",
            ExistingPeriodicWorkPolicy.KEEP,
            reminderRequest
        )
    }
}
