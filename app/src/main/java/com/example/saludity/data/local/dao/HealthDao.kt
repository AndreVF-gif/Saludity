package com.example.saludity.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.saludity.data.local.entity.ActivityEntity
import com.example.saludity.data.local.entity.MoodEntity
import com.example.saludity.data.local.entity.NutritionEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface HealthDao {

    // Activity
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity)

    @Query("DELETE FROM activities WHERE date = :date")
    suspend fun deleteActivity(date: LocalDate)

    @Query("SELECT * FROM activities WHERE date = :date")
    fun getActivityForDate(date: LocalDate): Flow<ActivityEntity?>

    @Query("SELECT * FROM activities ORDER BY date DESC")
    fun getAllActivities(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE isSynced = 0")
    suspend fun getUnsyncedActivities(): List<ActivityEntity>

    // Nutrition
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNutrition(nutrition: NutritionEntity)

    @Query("DELETE FROM nutrition WHERE date = :date")
    suspend fun deleteNutrition(date: LocalDate)

    @Query("SELECT * FROM nutrition WHERE date = :date")
    fun getNutritionForDate(date: LocalDate): Flow<NutritionEntity?>

    @Query("SELECT * FROM nutrition ORDER BY date DESC")
    fun getAllNutrition(): Flow<List<NutritionEntity>>

    @Query("SELECT * FROM nutrition WHERE isSynced = 0")
    suspend fun getUnsyncedNutrition(): List<NutritionEntity>

    // Mood
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMood(mood: MoodEntity)

    @Query("DELETE FROM moods WHERE date = :date")
    suspend fun deleteMood(date: LocalDate)

    @Query("SELECT * FROM moods WHERE date = :date")
    fun getMoodForDate(date: LocalDate): Flow<MoodEntity?>

    @Query("SELECT * FROM moods ORDER BY date DESC")
    fun getAllMoods(): Flow<List<MoodEntity>>

    @Query("SELECT * FROM moods WHERE isSynced = 0")
    suspend fun getUnsyncedMoods(): List<MoodEntity>
}
