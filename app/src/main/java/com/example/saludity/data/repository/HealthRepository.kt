package com.example.saludity.data.repository

import com.example.saludity.data.local.dao.HealthDao
import com.example.saludity.data.local.entity.ActivityEntity
import com.example.saludity.data.local.entity.MoodEntity
import com.example.saludity.data.local.entity.NutritionEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

class HealthRepository(
    private val healthDao: HealthDao,
    private val firebaseRepository: FirebaseRepository
) {

    fun getActivityForDate(date: LocalDate): Flow<ActivityEntity?> =
        healthDao.getActivityForDate(date)

    suspend fun insertActivity(activity: ActivityEntity) {
        healthDao.insertActivity(activity)
        val success = firebaseRepository.syncActivity(activity)
        if (success) {
            healthDao.insertActivity(activity.copy(isSynced = true))
        }
    }

    suspend fun deleteActivity(activity: ActivityEntity) {
        healthDao.deleteActivity(activity.date)
        firebaseRepository.deleteActivity(activity.date)
    }

    fun getNutritionForDate(date: LocalDate): Flow<NutritionEntity?> =
        healthDao.getNutritionForDate(date)

    suspend fun insertNutrition(nutrition: NutritionEntity) {
        healthDao.insertNutrition(nutrition)
        val success = firebaseRepository.syncNutrition(nutrition)
        if (success) {
            healthDao.insertNutrition(nutrition.copy(isSynced = true))
        }
    }

    suspend fun deleteNutrition(nutrition: NutritionEntity) {
        healthDao.deleteNutrition(nutrition.date)
        firebaseRepository.deleteNutrition(nutrition.date)
    }

    fun getMoodForDate(date: LocalDate): Flow<MoodEntity?> =
        healthDao.getMoodForDate(date)

    suspend fun insertMood(mood: MoodEntity) {
        healthDao.insertMood(mood)
        val success = firebaseRepository.syncMood(mood)
        if (success) {
            healthDao.insertMood(mood.copy(isSynced = true))
        }
    }

    suspend fun deleteMood(mood: MoodEntity) {
        healthDao.deleteMood(mood.date)
        firebaseRepository.deleteMood(mood.date)
    }

    fun getAllActivities(): Flow<List<ActivityEntity>> =
        healthDao.getAllActivities()

    fun getAllNutrition(): Flow<List<NutritionEntity>> =
        healthDao.getAllNutrition()

    fun getAllMoods(): Flow<List<MoodEntity>> =
        healthDao.getAllMoods()

    suspend fun syncAllUnsyncedData() {
        var allSuccess = true

        // Sync Activities
        val unsyncedActivities = healthDao.getUnsyncedActivities()
        unsyncedActivities.forEach { activity ->
            val success = firebaseRepository.syncActivity(activity)
            if (success) {
                healthDao.insertActivity(activity.copy(isSynced = true))
            } else {
                allSuccess = false
            }
        }

        // Sync Nutrition
        val unsyncedNutrition = healthDao.getUnsyncedNutrition()
        unsyncedNutrition.forEach { nutrition ->
            val success = firebaseRepository.syncNutrition(nutrition)
            if (success) {
                healthDao.insertNutrition(nutrition.copy(isSynced = true))
            } else {
                allSuccess = false
            }
        }

        // Sync Moods
        val unsyncedMoods = healthDao.getUnsyncedMoods()
        unsyncedMoods.forEach { mood ->
            val success = firebaseRepository.syncMood(mood)
            if (success) {
                healthDao.insertMood(mood.copy(isSynced = true))
            } else {
                allSuccess = false
            }
        }

        if (!allSuccess) {
            throw Exception("One or more items failed to sync to Firebase")
        }
    }

    suspend fun restoreDataFromCloud() {
        // Fetch from Firebase
        val activities = firebaseRepository.fetchAllActivities()
        val nutrition = firebaseRepository.fetchAllNutrition()
        val moods = firebaseRepository.fetchAllMoods()

        // Insert into Local Room (Room will handle overwriting if the primary key/date matches)
        activities.forEach { healthDao.insertActivity(it) }
        nutrition.forEach { healthDao.insertNutrition(it) }
        moods.forEach { healthDao.insertMood(it) }
    }
}
